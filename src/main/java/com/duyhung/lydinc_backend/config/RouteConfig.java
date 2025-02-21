package com.duyhung.lydinc_backend.config;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Component
public class RouteConfig {
    public static final List<String> PUBLIC_ROUTES = List.of(
            "/auth/**", "/drive/**", "/ws/**"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public boolean isPublicRoute(String requestURI) {
        return PUBLIC_ROUTES.stream()
                .anyMatch(route -> pathMatcher.match(route, requestURI.substring(4)));
    }
}
