package com.example.securitytest.configuration;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String token = jwtUtil.resolveToken(request);

        // 1. request 로 보낸 token 이 valid 한지 확인
        if (StringUtils.hasText(token) && jwtUtil.valid(token)) {
            // 2. valid 하면 authentication 을 SecurityContext 에 저장
            // 3. valid 하지 않으면 jwtUtil 내에서 Exception 이 던져진 상태 -> AuthenticationEntryPoint 에서 처리
            final Authentication authentication = jwtUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("JWT 인증 및 저장 완료 - authentication : {}", authentication);
        } else {
            log.info("token 이 올바르지 않음");
            log.info("token : [{}]", token);
            final Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            log.info("token 올바르지 않은데 Security context 에 Authentication 존재하는지 확인 ->");
            log.info("{}", authentication);
        }

        // 4. 나중에 권한을 확인하는 부분에서 Context 안의 Authentication 에서 권한 확인을 하는데 예외가 터지면 -> AccessDeniedHandler 에서 처리
        filterChain.doFilter(request, response);

    }

}
