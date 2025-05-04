package com.example.demo.security;

import com.example.demo.security.jwt.JwtProvider;
import com.example.demo.security.jwt.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.demo.security.jwt.JwtAuthenticationFilter.AUTHORIZATION_HEADER_KEY;
import static com.example.demo.security.jwt.JwtAuthenticationFilter.BEARER;

@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends OncePerRequestFilter {
    private static final String LOGOUT_URI = "/logout";
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);
        if(jwt != null){
            SecurityContextHolder.clearContext();
        }
        //리프레시 토큰 꺼내기
        //리프레시 토큰 DB에서 삭제
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER_KEY);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length()).trim();
        }
        throw new JwtException("Jwt 형식이 이상합니다.(Bearer 없음)");
    }
}
