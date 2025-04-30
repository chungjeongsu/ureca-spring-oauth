package com.example.demo.oauth.service;

import com.example.demo.entity.User;
import com.example.demo.oauth.repository.UserRepository;
import com.example.demo.oauth.token.CustomOAuth2User;
import com.example.demo.oauth.token.OAuth2Response;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * OAuth2.0에서 Access Token을 카카오톡/구글으로 보내서 사용자 정보를 받아옴
 * 각 스코프는 다르니, 각자 따로따로 파싱해줘, 저장해야함
 * 여기서 DB에 저장하든 뭐든 추가 처리 가능
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final OAuth2ResponseConverter scopeConverter;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2Response oAuth2Response = scopeConverter.convert(getRegistrationId(userRequest), oAuth2User);
        User existUser = userRepository.findByUserId(getOAuth2UserId(oAuth2Response));
        if(existUser == null) { //첫 로그인일 시 ID를 세션에 저장해두고, Success 시 권한 선택 창으로 gogo
            //userRepository.save(user)save 로직이 들어가야 함
            //setOnSession(user.user_id);
            //return new CustomOAuth2User();
        }
        //첫 로그인이 아닐 시 existUser로 처리 할 수 있겠다.

        //return CustomOAuth2User(existUser.getter~~);
    }

    private void setOnSession(Long userId){
        RequestContextHolder.currentRequestAttributes().setAttribute("FIRST_LOGIN", userId, RequestAttributes.SCOPE_SESSION);
    }

    private String getRegistrationId(OAuth2UserRequest userRequest){
        return userRequest.getClientRegistration().getRegistrationId();
    }

    private String getOAuth2UserId(OAuth2Response oAuth2Response) { //다른 Provider 같은 ProviderId 존재 가능성때문
        return oAuth2Response.getProvider() + oAuth2Response.getProviderId();
    }
}
