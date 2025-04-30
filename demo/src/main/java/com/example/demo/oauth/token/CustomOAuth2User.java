package com.example.demo.oauth.token;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * CustomOAuth2UserService에서 사용할 객체이다.
 * 모든 OAuth 사용자 인증은 Scope파라미터가 달라 하나로 파싱해주고 사용할 것이다.
 * 젠장, 너무 좋은걸?
 */
public class CustomOAuth2User implements OAuth2User {
    private final Map<String, Object> attributes;

    public CustomOAuth2User(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return "";
    }
}
