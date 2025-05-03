package com.example.demo.domain.service;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public UserRoleResponseDTO saveUserRole(Long userId, UserRoleRequestDTO userRoleRequestDTO,
                                          HttpServletResponse response) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found : " + userId));
        // 역할 변경
        user.setUserRole(userRoleRequestDTO.getRole());
        User savedUser = userRepository.save(user);

        // 새로운 JWT 발급 및 헤더 설정 (이전 토큰 대체)
        String newToken = jwtProvider.generateJwtToken(savedUser.getUserId(), savedUser.getUserRole());
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newToken);

        return new UserRoleResponseDTO(savedUser.getUserRole());
    }
}
