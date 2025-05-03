package com.example.demo.domain.service;

import com.example.demo.domain.entity.RefreshToken;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.entity.role.UserRole;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.security.jwt.JwtProvider;
import com.example.demo.security.jwt.RefreshTokenProvider;
import com.example.demo.security.jwt.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenProvider refreshTokenProvider;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken save(String rawRefreshToken) {
        RefreshToken refreshToken = getRefreshToken(rawRefreshToken);
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public String updateJwtToken(String rawRefreshToken, HttpServletResponse response) {
        RefreshToken dbRefreshToken = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new NoSuchElementException("Refresh token not found"));
        LocalDateTime now = LocalDateTime.now();
        if (dbRefreshToken.getExpiredAt().isBefore(now)) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        User user = dbRefreshToken.getUser();
        UserRole role = user.getUserRole();
        String newAccessToken = jwtProvider.generateJwtToken(user.getUserId(), role);
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

        return newAccessToken;
    }

    private RefreshToken getRefreshToken(String rawRefreshToken) {
        Optional<User> user = userRepository.findById(refreshTokenProvider.getUserId(rawRefreshToken));

        return RefreshToken.builder()
                .token(rawRefreshToken)
                .expiredAt(refreshTokenProvider.getExpiredAt(rawRefreshToken))
                .user(user.get())
                .build();
    }
}
