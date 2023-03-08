package com.example.securitytest.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JwtUtil {

    private final Long accessTokenExpirationTime;
    private final Long refreshTokenExpirationTime;
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration.token.access}") Long accessTokenExpirationTime,
                   @Value("${jwt.expiration.token.refresh}") Long refreshTokenExpirationTime) {
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateRefreshToken(@AuthenticationPrincipal PrincipalDetails user) {
        return generateToken(user, refreshTokenExpirationTime);
    }

    public String generateAccessToken(@AuthenticationPrincipal PrincipalDetails user) {
        return generateToken(user, accessTokenExpirationTime);
    }

    private String generateToken(PrincipalDetails user, long expirationTime) {
        return Jwts.builder()
                   .claim("nickname", user.getNickname())
                   .claim("email", user.getEmail())
                   .claim("provider", user.getProvider())
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

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public Authentication getAuthentication(String token) {
        final Claims claims = this.getClaims(token);

        PrincipalDetails user = PrincipalDetails.builder()
                                                .provider((String) claims.get("provider"))
                                                .email((String) claims.get("email"))
                                                .nickname((String) claims.get("nickname"))
                                                .build();

        return new UsernamePasswordAuthenticationToken(user, token, null);
    }

    public boolean valid(String token) {
        try {
            this.getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰");
        } catch (JwtException e) {
            log.info("유효하지 않은 토큰");
        } catch (Exception e) {
            log.info("예외 발생");
        }

        return false;
    }

}
