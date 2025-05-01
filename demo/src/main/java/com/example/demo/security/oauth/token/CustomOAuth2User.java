package com.example.demo.security.oauth.token;

import com.example.demo.security.oauth.repository.role.UserRole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            authorities.add(new SimpleGrantedAuthority(entry.getKey()));
        }
        return authorities;
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    public Long getUserId(){
        return (Long) attributes.get("id");
    }

    public UserRole getUserRole(){ //사실상 role은 하나이기 때문에 만들어줌
        return (UserRole) attributes.get("role");
    }
}
