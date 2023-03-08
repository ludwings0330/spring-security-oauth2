package com.example.securitytest.controller;

import lombok.extern.slf4j.Slf4j;
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

}
