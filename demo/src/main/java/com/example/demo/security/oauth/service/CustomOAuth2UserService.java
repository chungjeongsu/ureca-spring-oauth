package com.example.demo.security.oauth.service;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.domain.entity.role.UserRole;
import com.example.demo.security.oauth.token.CustomOAuth2User;
import com.example.demo.security.oauth.token.OAuth2Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * OAuth2 인증/인가 흐름에서 중요한 역할을 하는 커스텀 클래스
 * DefaultOAuth2UserService에서 loadUser를 하면, kakao 전용 Access Token(사용자 정보 API를 위한)을 통해 사용자 정보를 가져오고
 * 이를 OAuth2User로 반환해준다.
 * 이로 UserRepository에서 existUser를 통해 첫 로그인이 아닌지/맞는지를 검사
 * 첫 로그인이라면, ROLE_ANONYMOUS를 세팅해주고
 * 첫 로그인이 아니라면, existUser의 ROLE을 세팅해준다.
 * 그 후 CustomOAuth2User를 만들어 return해준다.
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

        Optional<User> existUser = userRepository
                .findByProviderAndProviderId(oAuth2Response.getProvider(), oAuth2Response.getProviderId());

        return existUser
                .map(user -> {
                    return new CustomOAuth2User(getAttributes(user));   //첫 로그인이 아니라면?
                })
                .orElseGet(() -> {                                      //첫 로그인이라면?
                    User newUser = createNewUser(oAuth2Response);
                    userRepository.save(newUser);
                    return new CustomOAuth2User(getAttributes(newUser));
                });
    }

    private String getRegistrationId(OAuth2UserRequest userRequest){
        return userRequest.getClientRegistration().getRegistrationId();
    }

    private Map<String, Object> getAttributes(User existUser) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userId", existUser.getUserId());
        attributes.put("role", existUser.getUserRole());
        return attributes;
    }

    //첫 로그인일때, ROLE_ANONYMOUS 권한을 가진 유저 생성(어찌보면 회원가입) => 아직 ROLE을 정하지 않았으니, ANONYMOUS
    private User createNewUser(OAuth2Response oAuth2Response) {
        return User.builder()
                .email(oAuth2Response.getEmail())
                .provider(oAuth2Response.getProvider())
                .providerId(oAuth2Response.getProviderId())
                .userRole(UserRole.ROLE_ANONYMOUS)
                .build();
    }
}
