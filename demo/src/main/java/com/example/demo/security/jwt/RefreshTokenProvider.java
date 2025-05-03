package com.example.demo.security.jwt;

import com.example.demo.domain.entity.role.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * JJWT를 사용한 RefreshTokenProvider 구현
 * 모든 필드들, 런타임 시점 초기화 즉 싱글톤 @Component
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

    //리프레시 토큰 생성
    public String generateRefreshToken(Long userId, UserRole userRole) {
        // 1) 토큰 생성
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityMs);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam(JwsHeader.ALGORITHM, SignatureAlgorithm.HS256.getValue())
                .claim("userId", userId)
                .claim("role", userRole.name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserId(String refreshToken){
        return getClaims(refreshToken).get("userId", Long.class);
    }

    public UserRole getRole(String refreshToken) {
        return getClaims(refreshToken).get("role", UserRole.class);
    }

    public LocalDateTime getExpiredAt(String refreshToken) {
        Date expiredAt = getClaims(refreshToken).getExpiration();
        return Instant.ofEpochMilli(expiredAt.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private Claims getClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}