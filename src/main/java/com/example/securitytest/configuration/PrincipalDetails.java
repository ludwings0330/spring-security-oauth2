package com.example.securitytest.configuration;

import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrincipalDetails implements OAuth2User, UserDetails {

    private String email;
    private String nickname;
    private String provider;

//    private Map<String, Object> attributes;

    // userDetails, OAuth2User
//    public PrincipalDetails(String email, String nickname, String provider) {
//        this.email = email;
//        this.nickname = nickname;
//        this.provider = provider;
//    }

    public static PrincipalDetails of(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "GOOGLE":
                return ofGoogle(attributes);
            default:
                return null;
        }
    }

    private static PrincipalDetails ofGoogle(Map<String, Object> attributes) {
        return PrincipalDetails.builder()
                               .provider("GOOGLE")
                               .email((String) attributes.get("email"))
                               .nickname((String) attributes.get("name"))
                               .build();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return this.email;
    }

    // OAuth2USer 구현
    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

}
