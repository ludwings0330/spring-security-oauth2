package com.example.securitytest.configuration;

import com.example.securitytest.model.Member;
import com.example.securitytest.repository.MemberRepository;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;

    private final JwtUtil jwtUtil;
    @Value("${app.oauth2.authorizedRedirectUrl}")
    private String redirectUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        log.info("OAuth2 Login Success");

        // 4. Access, Refresh Token 발급 -> Success Handler
        final PrincipalDetails user = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("user info in Security Context : {}", user);

        final String accessToken = jwtUtil.generateAccessToken(user);
        log.info("accessToken has generated : {}", accessToken);

        final String refreshToken = jwtUtil.generateRefreshToken(user);
        log.info("refreshToken has generated : {}", refreshToken);

        // 5. Refresh Token 은 DB 에 저장 -> Success Handler
        final Member member =
                memberRepository.findByEmailAndProvider(user.getEmail(), user.getProvider()).get();
        member.setRefreshToken(refreshToken);

        // 6. Access, Refresh Token 은 Client 에게 전달 -> Success Handler
        // 7. Client 가 JWT 인증을 받도록 Redirect 전송 -> Success Handler
        getRedirectStrategy()
                .sendRedirect(request, response, getRedirectUrlWithTokens(accessToken, refreshToken));
    }

    private String getRedirectUrlWithTokens(String accessToken, String refreshToken) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                                   .queryParam("accessToken", accessToken)
                                   .queryParam("refreshToken", refreshToken).build().toUriString();
    }

}
