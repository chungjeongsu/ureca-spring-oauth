package com.example.demo.security.config;

import com.example.demo.domain.entity.role.UserRole;
import com.example.demo.domain.service.RefreshTokenService;
import com.example.demo.security.jwt.JwtAuthenticationFilter;
import com.example.demo.security.jwt.JwtProvider;
import com.example.demo.security.jwt.RefreshTokenProvider;
import com.example.demo.security.oauth.handler.CustomAuthenticationFailureHandler;
import com.example.demo.security.oauth.handler.CustomOAuth2AuthenticationSuccessHandler;
import com.example.demo.security.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http    //OAuth2.0 로그인 흐름 설정
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                        .successHandler(new CustomOAuth2AuthenticationSuccessHandler(jwtProvider, refreshTokenProvider, refreshTokenService))
                        .failureHandler(new CustomAuthenticationFailureHandler()));

        http    //인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/refresh").permitAll()
                        .requestMatchers("user/role").hasAnyAuthority(UserRole.ROLE_ANONYMOUS.getValue())
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception{
        return new JwtAuthenticationFilter(jwtProvider);
    }
}
