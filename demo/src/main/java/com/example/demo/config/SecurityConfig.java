package com.example.demo.config;

import com.example.demo.oauth.handler.CustomAuthenticationFailureHandler;
import com.example.demo.oauth.handler.CustomAuthenticationSuccessHandler;
import com.example.demo.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .failureHandler(new CustomAuthenticationFailureHandler()));

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }


}
