package com.example.demo.security.jwt.refresh;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import org.springframework.security.core.Authentication;

/**
 * JJWT를 사용한 RefreshTokenProvider 구현
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenProvider {

    // Base64 인코딩된 256비트 시크릿 키 (application.yml에 설정)
    @Value("${jwt.secret}")
    private String secret;

    // 리프레시 토큰 유효기간 (밀리초)
    @Value("${jwt.refresh-token-validity-ms}")
    private long refreshTokenValidityMs;

    private Key key;

    /**
     * 빈 초기화 시점에 시크릿 키로 SecretKey 객체 생성
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Authentication에서 사용자 이름을 가져와 JWT Refresh Token 생성
     */
    public String generateRefreshToken(Authentication auth) {
        String username = auth.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityMs);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)                    // "typ":"JWT"
                .setHeaderParam(JwsHeader.ALGORITHM, SignatureAlgorithm.HS256.getValue())
                .setSubject(username)                                             // "sub": username
                .setIssuedAt(now)                                                 // "iat"
                .setExpiration(expiry)                                            // "exp"
                .signWith(key, SignatureAlgorithm.HS256)                          // HMAC-SHA256 서명
                .compact();                                                       // 토큰 생성
    }
}