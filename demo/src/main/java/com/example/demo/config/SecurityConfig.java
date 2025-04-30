package com.example.demo.config;

import com.example.demo.oauth.CustomAuthenticationSuccessHandler;
import com.example.demo.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .permitAll());

        http
                .oauth2Login(oauth -> oauth
                .loginPage("/login").userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                .successHandler(new CustomAuthenticationSuccessHandler()));
        return http.build();
    }
}
