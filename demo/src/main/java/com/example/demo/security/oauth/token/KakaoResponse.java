package com.example.demo.security.oauth.token;

import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoResponse implements OAuth2Response{
    private final static String KAKAO_PROVIDER = "kakao";
    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return KAKAO_PROVIDER;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return "ee";
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}
