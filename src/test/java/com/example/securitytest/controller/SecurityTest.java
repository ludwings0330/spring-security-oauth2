package com.example.securitytest.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityTest {


    @Test
    @DisplayName("jjwt_생성_복호화_테스트")
    public void jjwtTest() throws Exception {

        String token = Jwts.builder().setHeaderParam("type", "JWT")
                           .setHeaderParam("regDate", System.currentTimeMillis())
                           .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                           .setSubject("토큰 제목!")
                           .claim("email", "ludwings@naver.com")
                           .claim("nickname", "ludwings")
                           .signWith(SignatureAlgorithm.HS256, "mySecret1q2w3e4r!@htfcwt2fecqg1x3gfqysgfehjrb#$".getBytes())
                           .compact();

        System.out.println("token = " + token);

    }

}