package com.example.securitytest.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtUtil {

    private final String secret;
    private final Long accessTokenExpirationTime;
    private final Long refreshTokenExpirationTime;
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration.token.access}") Long accessTokenExpirationTime,
                   @Value("${jwt.expiration.token.refresh}") Long refreshTokenExpirationTime) {
        this.secret = secret;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateRefreshToken(@AuthenticationPrincipal PrincipalDetails user) {
        return generateToken(user, accessTokenExpirationTime);
    }

    public String generateAccessToken(@AuthenticationPrincipal PrincipalDetails user) {
        return generateToken(user, refreshTokenExpirationTime);
    }

    private String generateToken(PrincipalDetails user, long expirationTime) {
        return Jwts.builder()
                   .claim("nickname", "user.getNickname()")
                   .claim("email", "user.getEmail()")
                   .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        // Bearer -> JWT 또는 OAuth 인즈을 사용하는 경우
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length());
        }

        return null;
    }


    public Authentication getAuthentication(String accessToken) {
        return null;
    }

}
