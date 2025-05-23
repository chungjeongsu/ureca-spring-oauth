package com.example.demo.security.oauth.handler;

import com.example.demo.domain.service.RefreshTokenService;
import com.example.demo.security.jwt.JwtProvider;
import com.example.demo.security.jwt.RefreshTokenProvider;
import com.example.demo.domain.entity.role.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * Authentication에서 여러가지 정보를 가져와 JWT토큰을 생성해준다.
 * 또한 RefreshToken도 생성해준다.
 * 그 후, response Header에 담아 클라이언트로 보내준다.
 * 클라이언트는 이를 LocalStoreage에 담아 줄 것이다.
 * 매 요청마다, JWT 토큰만, 헤더에 담아 요청을 보내주어야 한다.
 */

@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    public static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    public static final String REFRESH_TOKEN_HEADER_KEY = "Refresh-Token";
    public static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        Long userId = (Long) principal.getAttributes().get("userId");
        UserRole role = UserRole.valueOf(principal.getAttributes().get("role").toString());
        String jwt = generateJwtToken(userId, role);                  //jwt 생성
        String refreshToken = generateRefreshToken(userId, role);     //refresh token 생성

        log.info("JWT {}", BEARER + jwt);
        log.info("Refresh-Token {}", refreshToken);

        refreshTokenService.save(refreshToken);                         //refresh token 저장한다. --> 서비스단에서 뭐, refreshTokenProvider 호출
        setResponseJwtAndRefreshToken(jwt, refreshToken, response);     //응답 헤더에 리프레시 토큰 넣기
    }

    private String generateJwtToken(Long userId, UserRole userRole) {
        return jwtProvider.generateJwtToken(userId, userRole);
    }

    private String generateRefreshToken(Long userId, UserRole userRole) {
        return refreshTokenProvider.generateRefreshToken(userId, userRole);
    }

    /*
    response에 jwt토큰과 refresh 토큰을 넣어주는 메서드
    만약, userRole이 ROLE_ANONYMOUS이면, BODY에 ROLE_ANONYMOUS를 넣어준다.
    클라이언트는 이를 보고, 요청 URL을 home으로 보내던가, 권한 선택 후 권한 업데이트 API로 보낸다.
     */
    private void setResponseJwtAndRefreshToken(String jwt, String refreshToken, HttpServletResponse response) throws IOException {
        response.setHeader(AUTHORIZATION_HEADER_KEY, BEARER + jwt);
        response.setHeader(REFRESH_TOKEN_HEADER_KEY, refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // ANONYMOUS 사용자인 경우 body에 role을 넣어줌
        UserRole role = jwtProvider.getRole(jwt);
        if (role == UserRole.ROLE_ANONYMOUS) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String body = String.format("{\"role\":\"%s\"}", role.getValue());
            response.getWriter().write(body);
        }
    }
}
