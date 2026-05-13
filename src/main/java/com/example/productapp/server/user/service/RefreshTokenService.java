package com.example.productapp.server.user.service;

import com.example.productapp.server.user.domain.RefreshToken;
import com.example.productapp.server.user.repository.RefreshTokenRepository;
import com.example.productapp.system.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public void deleteTokensByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElse(null);
    }

    @Transactional
    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public void logoutUser(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    public RefreshToken revokeAndGenerateRefreshToken(String refreshTokenStr) {
        // Rate limiting aici (ex. bucket4j / Redis) - simplificat aici
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token is revoked or expired");
        }

        // Rotation - revoke vechiul token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);


        String newRefreshTokenStr = jwtService.generateRefreshToken(refreshToken.getUserId());
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken(newRefreshTokenStr);
        newRefreshToken.setUserId(refreshToken.getUserId());
        newRefreshToken.setExpiresAt(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000L));
        return refreshTokenRepository.save(newRefreshToken);

    }

    public String generateNewAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token is revoked or expired");
        }

        // generează access token nou
        UserDetails user = customUserDetailsService.loadUserByUsername(refreshToken.getUserId());
        return jwtService.generateAccessToken(user);
    }

}
