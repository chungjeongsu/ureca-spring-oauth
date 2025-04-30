package com.example.demo.oauth.token;

import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoResponse implements OAuth2Response{
    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "";
    }

    @Override
    public String getProviderId() {
        return "";
    }

    @Override
    public String getEmail() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }
}
