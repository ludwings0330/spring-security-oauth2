package com.example.securitytest.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring()
                         .antMatchers(HttpMethod.OPTIONS, "**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterAfter(jwtAuthenticationFilter, LogoutFilter.class);

        http.exceptionHandling(handle ->
                                       handle
                                               .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                               .accessDeniedHandler(jwtAccessDeniedHandler));

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.oauth2Login(oauth2 ->
                                 oauth2
                                         .userInfoEndpoint() // oauth2 로그인 성공후 유저 정보를 가져온다.
                                         .userService(customOAuth2UserService) // 가져온 유저 정보를 기반으로 userService 에서 회원인지 확인
                                         .and()
                                         .successHandler(customAuthenticationSuccessHandler) // 인증 되었으면 처리
                                         .failureHandler(customAuthenticationFailureHandler)); // 인증되지 않으면 처리

        http.authorizeRequests(request ->
                                       request
                                               .antMatchers("/login/**", "/redirect/**")
                                               .permitAll()
                                               .anyRequest()
                                               .authenticated());

        http.csrf().disable();

        return http.build();
    }

}
