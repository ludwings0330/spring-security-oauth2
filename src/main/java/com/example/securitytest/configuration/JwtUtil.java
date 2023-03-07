package com.example.securitytest.configuration;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public String generateRefreshToken() {
        return "";
    }

    public String generateAccessToken() {
        return "";
    }

    public String resolveToken(HttpServletRequest request) {

        return "";
    }


    public Authentication getAuthentication(String accessToken) {

        return null;
    }

}
