package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.exception.JwtValidationException;
import com.duyhung.lydinc_backend.utils.CookieUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final CookieUtils cookieUtils;
    @Value("${jwt.secret-key}")
    String secretKey;
    @Value("${jwt.accessToken-expiration}")
    private Long ACCESS_TOKEN_EXPIRY_DATE;
    @Value("${jwt.refreshToken-expiration}")
    private Long REFRESH_TOKEN_EXPIRY_DATE;

    private SecretKey getSecretKey() {
        SecretKey key = null;
        if (secretKey != null) {
            key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY_DATE))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY_DATE))
                .signWith(getSecretKey())
                .compact();
    }


    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean verifyToken(String token) {
        try {
            String username = getUsername(token);
            return username != null && !username.isEmpty();
        } catch (SignatureException e) {
            throw new JwtValidationException("Invalid JWT signature.", e);
        } catch (MalformedJwtException e) {
            throw new JwtValidationException("Invalid JWT token.", e);
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("JWT token is expired.", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtValidationException("JWT token is unsupported.", e);
        } catch (IllegalArgumentException e) {
            throw new JwtValidationException("JWT claims string is empty.", e);
        }
    }


}