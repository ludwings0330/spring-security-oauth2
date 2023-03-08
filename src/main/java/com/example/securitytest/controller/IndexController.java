package com.example.securitytest.controller;

import com.example.securitytest.configuration.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class IndexController {

    @GetMapping("/redirect")
    public String redirectFromOauth(@RequestParam String accessToken, String refreshToken) {
        log.info("accessToken : {}", accessToken);
        log.info("refreshToken: {}", refreshToken);
        return String.format("accessToken : %s , refreshToken : %s", accessToken, refreshToken);
    }

    @GetMapping("/jwt/authentication/test")
    public String jwtAuthenticationTest(@AuthenticationPrincipal PrincipalDetails user) {
        log.info("jwt 인증 완료하여 컨트롤러 진입");
        log.info("principaldetail : {}", user);

        return user.toString();
    }

}
