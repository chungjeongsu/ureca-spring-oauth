package com.example.demo.security.oauth.service;

import com.example.demo.security.oauth.token.GoogleResponse;
import com.example.demo.security.oauth.token.KakaoResponse;
import com.example.demo.security.oauth.token.OAuth2Response;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ResponseConverter {
    public OAuth2Response convert(String registrationId, OAuth2User target) {
        OAuth2Response oAuth2Response = null;

        if(registrationId.equals("Google")){
            return oAuth2Response = new GoogleResponse(target.getAttributes());
        }
        if(registrationId.equals("kakao")){
            return oAuth2Response = new KakaoResponse(target.getAttributes());
        }
        return oAuth2Response;
    }
}
