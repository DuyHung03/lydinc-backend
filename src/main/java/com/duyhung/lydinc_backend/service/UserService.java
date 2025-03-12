package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Role;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.PaginationResponse;
import com.duyhung.lydinc_backend.model.dto.UserDto;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Value("${redis.reset-password-base-key}")
    private String redisResetPwBaseKey;

    public PaginationResponse<UserDto> getAllAccounts(String searchValue, Integer universityId, Integer orderBy, int pageNo, int pageSize) {
        // Determine sorting order
        Sort sort = Sort.unsorted();
        if (orderBy != null) {
            if (orderBy == 1) {
                sort = Sort.by(Sort.Direction.ASC, "username");
            } else if (orderBy == 2) {
                sort = Sort.by(Sort.Direction.DESC, "username");
            }
        }

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

}