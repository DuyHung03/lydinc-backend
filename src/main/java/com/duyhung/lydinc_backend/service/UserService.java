package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Role;
import com.duyhung.lydinc_backend.model.University;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.auth.RegisterRequest;
import com.duyhung.lydinc_backend.model.dto.PaginationResponse;
import com.duyhung.lydinc_backend.model.dto.UserDto;
import com.duyhung.lydinc_backend.repository.RoleRepository;
import com.duyhung.lydinc_backend.repository.UniversityRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import com.duyhung.lydinc_backend.service.redis.RedisService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractService {

    private static final Logger logger = LogManager.getLogger(ModuleService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final RoleRepository roleRepository;
    private final UniversityRepository universityRepository;

    @Value("${redis.reset-password-base-key}")
    private String redisResetPwBaseKey;

    @Transactional
    public String createStudentAccounts(List<RegisterRequest> requests) {
        requests.forEach(request -> {
            logger.info("Creating account for username: {}", request.getUsername());

            Optional<User> existingUser = userRepository.checkUserExist(request.getUsername());
            if (existingUser.isPresent()) {
                logger.error("User {} already exists", request.getUsername());
                throw new RuntimeException("User " + request.getUsername() + " already exist!");
            }

            University university = universityRepository.findById(request.getUniversityId())
                    .orElseThrow(() -> {
                        logger.error("University with ID {} not found", request.getUniversityId());
                        return new RuntimeException("University not found");
                    });

            String password = request.getPassword() == null ? generateRandomPassword() : request.getPassword();

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(password))
                    .name(request.getFaculty())
                    .isPasswordChanged(0)
                    .isAccountGranted(1)
                    .university(university)
                    .roles(new HashSet<>())
                    .build();

            Role userRole = roleRepository.findByRoleId(1);
            user.getRoles().add(userRole);

            userRepository.save(user);
            logger.info("User {} registered successfully", request.getUsername());

            try {
                emailService.sendEmailAccountGranted(request.getEmail(), request.getUsername(), password);
                logger.info("Account creation email sent to {}", request.getEmail());
            } catch (MessagingException e) {
                logger.error("Error sending email to {}: {}", request.getEmail(), e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        return "Create account and send email successfully!";
    }

    public PaginationResponse<UserDto> getAllAccounts(String searchValue, Integer universityId, Integer orderBy, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userPage;

        if (searchValue != null) {
            userPage = userRepository.searchByUsername(searchValue, pageable);
        } else if (universityId == null) {
            userPage = userRepository.findAllStudents(pageable);
        } else {
            userPage = userRepository.findByUniversityId(universityId, pageable);
        }

        List<UserDto> users = userPage.getContent().stream().map(this::mapUserToDto).toList();

        return new PaginationResponse<>(
                users,
                userPage.getTotalPages(),
                pageNo + 1,
                pageSize
        );
    }


    public UserDto getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.builder()
                .username(user.getUsername())
                .roles(user.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toSet()))
                .isAccountGranted(user.getIsAccountGranted())
                .isPasswordChanged(user.getIsPasswordChanged())
                .universityId(user.getUniversity() != null ? user.getUniversity().getUniversityId() : null)
                .universityName(user.getUniversity() != null ? user.getUniversity().getShortName() : null)
                .build();
    }

    @Transactional
    public String changePassword(String newPassword, String token) throws MessagingException {
        if (!jwtService.verifyToken(token)) {
            throw new RuntimeException("Invalid RP Token");
        }
        String username = jwtService.getUsername(token);
        String key = redisResetPwBaseKey + username;
        String redisToken = redisService.getCacheValue(key);
        if (!token.equals(redisToken)) {
            throw new RuntimeException("Invalid RP Token");
        }

        // Find the user by Username
        User user = authorizeUserByUsername(username);

        // Check if the new password is the same as the old one
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("New password should not be the same as the current password");
        }

        // Encode the new password
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        // Save the updated user
        userRepository.saveNewPassword(encodedPassword, 1, user.getUserId());
        redisService.deleteCache(key);
        emailService.sendEmailChangePassword(user.getEmail(), user.getUsername());

        return "Password changed successfully!";
    }

    public List<UserDto> getAllLecturers() {
        try {
            logger.info("Fetching all students from database");
            List<User> users = userRepository.findAllLecturers();
            logger.info("Found {} Lecturers", users.size());

            return users.stream().map(user -> UserDto.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build()).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while fetching Lecturers: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching Lecturers");
        }
    }

    @Transactional
    public void createNewLecturer(
            String username,
            String fullName,
            String email,
            String phone
    ) {
        String password = generateRandomPassword();
        User user = User.builder()
                .username(username)
                .name(fullName)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .isAccountGranted(1)
                .isPasswordChanged(0)
                .roles(new HashSet<>())
                .build();
        Role userRole = roleRepository.findByRoleId(2);
        user.getRoles().add(userRole);
        userRepository.save(user);

        try {
            emailService.sendEmailAccountGranted(email, username, password);
            logger.info("Account creation email sent to {}", email);
        } catch (MessagingException e) {
            logger.error("Error sending email to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public List<UserDto> getAllStudents() {
        try {
            logger.info("Fetching all students from database");
            List<User> users = userRepository.findStudentsWithoutUniversity();
            logger.info("Found {} students", users.size());

            return users.stream().map(user -> UserDto.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build()).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while fetching students: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching users");
        }
    }

    public String sendEmailResetPw(String username) throws MessagingException {
        User user = authorizeUserByUsername(username);
        if (user.getEmail() == null) {
            throw new RuntimeException("Email for {} not found" + username);
        }
        String resetUrl = generateResetPasswordUrl(username);
        emailService.sendLinkResetPassword(user.getEmail(), user.getUsername(), resetUrl);
        return user.getEmail();
    }

    public String getResetPasswordUrl(String username) {
        authorizeUserByUsername(username);
        return generateResetPasswordUrl(username);
    }

    private String generateResetPasswordUrl(String username) {
        String resetPwToken = jwtService.generateResetPasswordToken(username);
        String key = redisResetPwBaseKey + username;
        redisService.saveRPTokenToCache(key, resetPwToken);

        return "http://localhost:5173/reset-password?token=" + resetPwToken;
    }

    private User authorizeUserByUsername(String username) {
        return userRepository.checkUserExist(username).orElseThrow(
                () -> new RuntimeException("Username not found")
        );
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        logger.debug("Generated random password");
        return password.toString();
    }

}