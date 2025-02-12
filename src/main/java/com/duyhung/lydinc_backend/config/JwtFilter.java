package com.duyhung.lydinc_backend.config;

import com.duyhung.lydinc_backend.exception.JwtValidationException;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.service.JwtService;
import com.duyhung.lydinc_backend.service.UserDetailsServiceImp;
import com.duyhung.lydinc_backend.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImp userDetailsService;
    private final CookieUtils cookieUtils;
    private final RouteConfig routeConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Skip token validation for public routes
        if (routeConfig.isPublicRoute(requestURI)) {
            filterChain.doFilter(request, response); // Continue the filter chain
            return;
        }

        // For non-public routes, validate the token
        try {
            String accessToken = cookieUtils.getCookie(request, "accessToken");

            if (accessToken == null) {
                throw new JwtValidationException("Token not found");
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtService.getUsername(accessToken);

                if (username != null && jwtService.verifyToken(accessToken)) {
                    User userDetails = (User) userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new JwtValidationException("Invalid token");
                }
            }

            filterChain.doFilter(request, response);
        } catch (JwtValidationException ex) {
            // Handle the exception and send a 401 response
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(ex.getMessage());
        }
    }
}
