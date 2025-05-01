package com.example.demo.security.oauth.handler;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 성공시 /home으로 redirect
 * 첫 로그인 하는 사용자일 경우 DB에 저장해야함
 * 이때, 사장으로 권한을 가져갈 것인가? 아니면, 유저로 권한을 가져갈 것인가? 설정 가능 => 관리자는 나중에 생각해보자.
 * 여기서 JWT를 생성하거나, 쿠키에 기본적인 사용자 정보를 포함하거나를 할 수 있겠다.
 * 현재는 OAUTh만 쓰니, 기본적 사용자 정보를 쿠키에 포함해주자.
 * 만약, Rest가 아닌, 서버 사이드 렌더링을 쓴다면, 젠장이다. 세션에 저장해주자.
 *
 */

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final String SUCCESS_REDIRECT_URL = "/home";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.sendRedirect(SUCCESS_REDIRECT_URL);
    }

    private boolean isNewLogin() {
        return RequestContextHolder
                .currentRequestAttributes()
                .getAttribute("FIRST_LOGIN", RequestAttributes.SCOPE_SESSION) != null;
    }
}
