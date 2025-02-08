package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.exception.AuthValidationException;
import com.duyhung.lydinc_backend.exception.JwtValidationException;
import com.duyhung.lydinc_backend.model.Role;
import com.duyhung.lydinc_backend.model.University;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.auth.AuthResponse;
import com.duyhung.lydinc_backend.model.auth.LoginRequest;
import com.duyhung.lydinc_backend.model.auth.RegisterRequest;
import com.duyhung.lydinc_backend.model.dto.UserDto;
import com.duyhung.lydinc_backend.repository.RoleRepository;
import com.duyhung.lydinc_backend.repository.UniversityRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import com.duyhung.lydinc_backend.utils.CookieUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService extends AbstractService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtils;
    private final CookieUtils cookieUtils;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final UniversityRepository universityRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final CourseService courseService;


    @Value("${jwt.accessToken-expiration}")
    private int ACCESS_TOKEN_EXPIRY_DATE;

    @Value("${jwt.refreshToken-expiration}")
    private int REFRESH_TOKEN_EXPIRY_DATE;

//    public String signIn(RegisterRequest request, Integer ) {
//        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
//        if (existingUser.isPresent()) {
//            return "Username is already taken.";
//        }
//
//        University university = universityRepository.findById(request.getUniversityId())
//                .orElseThrow(() -> new RuntimeException("University not found"));
//
//        User user = User.builder()
//                .username(request.getUsername())
//                .email(request.getEmail())
//                .phone(request.getPhone())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .name(request.getName())
//                .isPasswordFirstChanged(0)
//                .isAccountGranted(1)
//                .university(university)
//                .roles(new HashSet<>())
//                .build();
//
//        Role userRole = roleRepository.findByRoleId(1);
//        user.getRoles().add(userRole);
//
//        userRepository.save(user);
//
//        return "User registered successfully!";
//    }

    public String createAccount(List<RegisterRequest> requests) {
        requests.forEach(request ->
        {

            Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent()) {
                throw new RuntimeException("User" + request.getUsername() + "already exist!");
            }

            University university = universityRepository.findById(request.getUniversityId())
                    .orElseThrow(() -> new RuntimeException("University not found"));

            String password = request.getPassword() == null
                    ? generateRandomPassword()
                    : request.getPassword();

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(password))
                    .name(request.getFaculty())
                    .isPasswordFirstChanged(0)
                    .isAccountGranted(1)
                    .university(university)
                    .roles(new HashSet<>())
                    .build();

            Role userRole = roleRepository.findByRoleId(1);
            user.getRoles().add(userRole);

            userRepository.save(user);
            try {
                emailService.sendEmailAccountGranted(
                        request.getEmail(),
                        request.getUsername(),
                        password
                );
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

        });
        return "Create account and send email successfully!";
    }

    public AuthResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Extract user details from authentication object
            User user = (User) authentication.getPrincipal();

            // Generate tokens
            String accessToken = jwtUtils.generateAccessToken(user.getUsername());
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

            // Set cookies for tokens
            cookieUtils.setCookie("accessToken", accessToken, ACCESS_TOKEN_EXPIRY_DATE / 1000, response);
            cookieUtils.setCookie("refreshToken", refreshToken, REFRESH_TOKEN_EXPIRY_DATE / 1000, response);

            UserDto userDto = new UserDto();
            userDto.setUserId(user.getUserId());
            userDto.setUsername(user.getUsername());
            userDto.setIsPasswordFirstChanged(user.getIsPasswordFirstChanged());
            userDto.setIsAccountGranted(user.getIsAccountGranted());
            userDto.setRoles(user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toSet()));

            // Build response
            AuthResponse authResponse = new AuthResponse();
            authResponse.setCode(HttpStatus.OK.value());
            authResponse.setUser(userDto);
            return authResponse;

        } catch (BadCredentialsException e) {
            throw new AuthValidationException("Invalid username or password");
        }
    }


    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = cookieUtils.getCookie(request, "refreshToken");
            if (refreshToken == null || !jwtService.verifyToken(refreshToken)) {
                throw new JwtValidationException("Unauthorized");
            }
            String newAccessToken = jwtService.generateAccessToken(jwtService.getUsername(refreshToken));
            cookieUtils.setCookie("accessToken", newAccessToken, 86400, response);

            return "Refresh token successfully!";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }


}
