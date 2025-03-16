package com.duyhung.lydinc_backend.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {
    public void setCookie(
            String name,
            String token,
            int expiry,
            HttpServletResponse response,
            String path
    ) {
        Cookie cookie = new Cookie(name, token);
        cookie.setMaxAge(expiry);
        cookie.setPath(path);
        response.addCookie(cookie);
    }

    public String getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
