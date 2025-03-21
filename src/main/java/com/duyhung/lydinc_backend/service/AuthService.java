package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.exception.AuthValidationException;
import com.duyhung.lydinc_backend.exception.JwtValidationException;
import com.duyhung.lydinc_backend.model.Role;
import com.duyhung.lydinc_backend.model.University;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.auth.LoginRequest;
import com.duyhung.lydinc_backend.model.auth.RegisterRequest;
import com.duyhung.lydinc_backend.repository.RoleRepository;
import com.duyhung.lydinc_backend.repository.UniversityRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import com.duyhung.lydinc_backend.utils.CookieUtils;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

@Service
@RequiredArgsConstructor
public class AuthService extends AbstractService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtils;
    private final CookieUtils cookieUtils;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @Value("${jwt.accessToken-expiration}")
    private int ACCESS_TOKEN_EXPIRY_DATE;

    @Value("${jwt.refreshToken-expiration}")
    private int REFRESH_TOKEN_EXPIRY_DATE;

    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletResponse response) {
        try {
            logger.info("Attempting login for username: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()
                    ));

            User user = (User) authentication.getPrincipal();
            if (user.getIsAccountGranted().equals(1) && user.getIsPasswordChanged().equals(0)) {
                return ResponseEntity.ok(userService.getResetPasswordUrl(user.getUsername()));
            }
            String accessToken = jwtUtils.generateAccessToken(user.getUsername(), user.getUserId());
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername(), user.getUserId());

            cookieUtils.setCookie("accessToken", accessToken, ACCESS_TOKEN_EXPIRY_DATE / 1000, response, "/");
            cookieUtils.setCookie("refreshToken", refreshToken, REFRESH_TOKEN_EXPIRY_DATE / 1000, response, "auth/refreshToken");
            logger.info("User {} logged in successfully", user.getUsername());
            return ResponseEntity.ok(true);
        } catch (BadCredentialsException e) {
            logger.error("Invalid login attempt for username: {}", loginRequest.getUsername());
            throw new AuthValidationException("Invalid username or password");
        }
    }

    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            logger.info("Attempting to refresh token");

            // Get refresh token from cookies
            String refreshToken = cookieUtils.getCookie(request, "refreshToken");
            if (refreshToken == null || !jwtService.verifyToken(refreshToken)) {
                logger.error("Invalid or missing refresh token");
                throw new JwtValidationException("Unauthorized");
            }

            // Extract user info from the refresh token
            Claims claims = jwtService.getClaimsFromToken(refreshToken);
            String username = claims.getSubject();
            String userId = claims.get("id", String.class); // Extract userId from claims

            // Generate new access token with user info
            String newAccessToken = jwtService.generateAccessToken(username, userId);

            // Set new access token in cookies
            cookieUtils.setCookie("accessToken", newAccessToken, 86400, response, "/");

            logger.info("Token refreshed successfully");
            return "Refresh token successfully!";
        } catch (JwtValidationException e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String register(
            String username,
            String fullName,
            String password,
            String email,
            String phone
    ) {
        Optional<User> existingUser = userRepository.checkUserExist(username);
        if (existingUser.isPresent()) {
            logger.error("User {} already exists", username);
            throw new RuntimeException("User " + username + " already exist!");
        }
        User user = User.builder()
                .username(username)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .name(fullName)
                .isPasswordChanged(0)
                .isAccountGranted(0)
                .roles(new HashSet<>())
                .build();

        Role userRole = roleRepository.findByRoleId(1);
        user.getRoles().add(userRole);

        userRepository.save(user);
        logger.info("User {} registered successfully", username);

        return "Create account successfully!";

    }
}
