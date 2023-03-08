package com.example.securitytest.configuration;

import com.example.securitytest.model.Member;
import com.example.securitytest.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    // OAuth 2.0 로그인 성공시 loadUser 를 통해 확인
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth 인증 성공시 호출");
        log.info("accessToken : {}", userRequest.getAccessToken().getTokenValue());

        // 1. nickname, email, provider 획득
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        // provider 획득
        final String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        log.info("provider : [{}]", provider);

        final Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("attributes : {}", attributes);

        // Provider 마다 OAuth2User 를 만드는 방식이 다르기 때문에 처리
        final PrincipalDetails details = PrincipalDetails.of(provider, attributes);

        // 2. 최초로 로그인 하는지 확인
        if (memberRepository
                .findByEmailAndProvider(details.getEmail(), details.getProvider())
                .isPresent()) {
            log.info("이미 존재하는 회원");
        } else {
            log.info("존재하지 않는 회원 -> 회원가입 진행");
            final Member member = Member.builder()
                                        .provider(details.getProvider())
                                        .nickname(details.getNickname())
                                        .email(details.getEmail())
                                        .build();

            // 3. 최초 로그인 이면 DB 에 저장
            memberRepository.save(member);
        }

        // 4. Access, Refresh Token 발급 -> Success Handler
        // 5. Refresh Token 은 DB 에 저장 -> Success Handler
        // 6. Access, Refresh Token 은 Client 에게 전달 -> Success Handler
        // 7. Client 가 JWT 인증을 받도록 Redirect 전송 -> Success Handler

        return details;
    }

}
