package com.example.demo.security.oauth.service;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.security.jwt.refresh.RefreshTokenRepository;
import com.example.demo.security.oauth.repository.role.UserRole;
import com.example.demo.security.oauth.token.CustomOAuth2User;
import com.example.demo.security.oauth.token.OAuth2Response;
import com.example.demo.security.oauth.token.RefreshToken;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * refresh 토큰 생성저장도 여기서함
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final OAuth2ResponseConverter scopeConverter;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2Response oAuth2Response = scopeConverter.convert(getRegistrationId(userRequest), oAuth2User);

        Optional<User> existUser = userRepository
                .findByProviderAndProviderId(oAuth2Response.getProvider(), oAuth2Response.getProviderId());

        if(existUser.isPresent()) { //첫 로그인이 아닐 시 existUser로 처리 할 수 있겠다.
            return new CustomOAuth2User(oAuth2User.getAttributes());
        }
        User user = getNewUser(oAuth2Response); //첫 로그인일 시 user에 권한을 포함해야함
        userRepository.save(user);
        return new CustomOAuth2User(getCopiedAttributes(user, oAuth2User));
    }

    private User getNewUser(OAuth2Response oAuth2Response) {
        return User.builder()
                .email(oAuth2Response.getEmail())
                .provider(oAuth2Response.getProvider())
                .providerId(oAuth2Response.getProviderId())
                .userRole(getUserRole())
                .build();
    }

    private String getRegistrationId(OAuth2UserRequest userRequest){
        return userRequest.getClientRegistration().getRegistrationId();
    }

    private UserRole getUserRole(){
        String choiceRole = (String) RequestContextHolder
                .currentRequestAttributes()
                .getAttribute("role", RequestAttributes.SCOPE_SESSION);

        //메모리관리를 위해 삭제
        RequestContextHolder.currentRequestAttributes().removeAttribute("role", RequestAttributes.SCOPE_SESSION);

        return EnumSet.allOf(UserRole.class).stream()
                .filter(role -> {return choiceRole.equals(role.name());})
                .findFirst()
                .get();
    }

    private Map<String, Object> getCopiedAttributes(User user, OAuth2User oAuth2User){  //oAuth2User 객체는 불변이기에, 복사해야한다.
        Map<String, Object> copiedAttributes = new HashMap<>(oAuth2User.getAttributes());
        copiedAttributes.put("role", user.getUserRole());
        return copiedAttributes;
    }
}
