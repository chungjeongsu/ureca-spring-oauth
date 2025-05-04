package com.example.demo.security.jwt;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    public static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;

    /**
     * 설명 : SecurityConfig에서 인증이 필요한 URI에 대해 모든 요청에 인증을 진행한다.
     * 요청 헤드에서 Authorization : Bearer {String 타입 JWT 토큰}에서 {String 타입 JWT 토큰}만 가져온다.
     * 가져온 JWT 토큰의 유효성 검사를 한다.
     * => JWT 토큰이 아예 없거나 이상한 경우 : OAUTH2 인증 흐름으로 진행한다.(로그인 페이지 리다이렉트) == doFilter()
     * => JWT 토큰이 있지만 만료된 경우 : refresh 토큰을 통한 JWT 토큰 갱신 흐름 == 401 예외 response에 넣기
     * => JWT 토큰이 정상적으로 있는 경우 : JWT의 claims를 Authentication토큰에 넣어주고, 인가 흐름 == SecurityContextHolder 채우고 doFilter
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{ //JWT 토큰이 정상적으로 있는 경우
            String jwt = resolveToken(request);
            if(jwt == null) throw new JwtException("Jwt가 없음"); //아래 JWT 토큰이 아예 없거나 이상한 경우로 진행. 이게 없으면 NullPointerException
            CustomUserDetails customUserDetails = getCustomUserDetails(jwt);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){ //JWT 토큰이 있지만 만료된 경우
            setResponse(response);  //응답에 401에러 만들어준다.
            return;
        }catch (JwtException | IllegalArgumentException e) {    //JWT 토큰이 아예 없는 경우
            SecurityContextHolder.clearContext();   //컨텍스트 정리 OAuth2 인증 흐름 진행
            filterChain.doFilter(request, response);
        }catch (Exception e) {
            log.error("알 수 없는 에러 발생 {}" + e.getMessage());
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }

    private void setResponse(HttpServletResponse response) throws IOException {
        response.setStatus(SC_UNAUTHORIZED);
        response.setHeader("Token-Expired", "true");
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write("{"
                + "\"status\":401,"
                + "\"error\":\"Unauthorized\","
                + "\"message\":\"Access token expired\""
                + "}");
    }

    //UsernamePasswordAuthenticationToken을 생성하기 위한 정보들 Jwt에서 파싱
    private CustomUserDetails getCustomUserDetails(String jwt) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("role", jwtProvider.getRole(jwt));
        userDetails.put("userId", jwtProvider.getUserId(jwt));
        return new CustomUserDetails(userDetails);
    }

    //토큰은 헤더에 Authorization: Bearer ey어쩌구~~로 온다. Bearer를 빼주고, ey어쩌구~~만 가져온다. ey어쩌구~~는 인코딩된 Jwt토큰임
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER_KEY);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length()).trim();
        }
        throw new JwtException("Jwt 형식이 이상합니다.(Bearer 없음)");
    }
}
