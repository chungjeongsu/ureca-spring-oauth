package com.example.demo.security.jwt.refresh;

import com.example.demo.security.oauth.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
