package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.exception.AuthValidationException;
import com.duyhung.lydinc_backend.exception.JwtValidationException;
import com.duyhung.lydinc_backend.model.Role;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.auth.LoginRequest;
import com.duyhung.lydinc_backend.repository.RoleRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import com.duyhung.lydinc_backend.utils.CookieUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.HashSet;
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

    /**
     * Handles user login authentication.
     *
     * @param loginRequest User credentials.
     * @param response     HTTP response to set cookies.
     * @return Response entity indicating success or failure.
     */
    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletResponse response) {
        try {
            logger.info("Attempting login for username: {}", loginRequest.getUsername());

            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()
                    ));

            User user = (User) authentication.getPrincipal();

            // Check if the user needs to reset their password
            if (user.getIsAccountGranted().equals(1) && user.getIsPasswordChanged().equals(0)) {
                logger.warn("User {} must reset password before proceeding", user.getUsername());
                return ResponseEntity.ok(userService.getResetPasswordUrl(user.getUsername()));
            }

            // Generate tokens
            String accessToken = jwtUtils.generateAccessToken(user.getUsername(), user.getUserId());
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername(), user.getUserId());

            // Set tokens in cookies
            cookieUtils.setCookie("accessToken", accessToken, ACCESS_TOKEN_EXPIRY_DATE / 1000, response, "/");
            cookieUtils.setCookie("refreshToken", refreshToken, REFRESH_TOKEN_EXPIRY_DATE / 1000, response, "auth/refreshToken");

            logger.info("User {} logged in successfully", user.getUsername());
            return ResponseEntity.ok(true);
        } catch (BadCredentialsException e) {
            logger.error("Invalid login attempt for username: {}", loginRequest.getUsername());
            throw new AuthValidationException("Invalid username or password");
        }
    }

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param request  HTTP request to extract cookies.
     * @param response HTTP response to set new cookies.
     * @return Success message if refresh is successful.
     */
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            logger.info("Attempting to refresh token");

            // Retrieve refresh token from cookies
            String refreshToken = cookieUtils.getCookie(request, "refreshToken");
            if (refreshToken == null || !jwtService.verifyToken(refreshToken)) {
                logger.error("Invalid or missing refresh token");
                throw new JwtValidationException("Unauthorized");
            }

            // Extract user information from token
            Claims claims = jwtService.getClaimsFromToken(refreshToken);
            String username = claims.getSubject();
            String userId = claims.get("id", String.class);

            // Generate new access token
            String newAccessToken = jwtService.generateAccessToken(username, userId);

            // Set new token in cookies
            cookieUtils.setCookie("accessToken", newAccessToken, 86400, response, "/");

            logger.info("Token refreshed successfully for user: {}", username);
            return "Refresh token successfully!";
        } catch (JwtValidationException e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Registers a new user account.
     *
     * @param username User's chosen username.
     * @param fullName User's full name.
     * @param password User's password.
     * @param email    User's email.
     * @param phone    User's phone number.
     * @return Success message if registration is successful.
     */
    public String register(
            String username,
            String fullName,
            String password,
            String email,
            String phone
    ) {
        logger.info("Registering new user: {}", username);
        Optional<User> existingUser = userRepository.checkUserExist(username);

        if (existingUser.isPresent()) {
            logger.error("User {} already exists", username);
            throw new RuntimeException("User " + username + " already exists!");
        }

        // Create new user instance
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

        // Assign default role
        Role userRole = roleRepository.findByRoleId(1);
        user.getRoles().add(userRole);

        // Save user to the database
        userRepository.save(user);
        logger.info("User {} registered successfully", username);

        return "Create account successfully!";
    }
}
