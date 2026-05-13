package com.example.productapp.system.security.oauth2;

import com.example.productapp.server.user.domain.RefreshToken;
import com.example.productapp.server.user.service.RefreshTokenService;
import com.example.productapp.system.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
@AllArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = (String) oAuth2User.getAttributes().get("email");

        String refreshToken = jwtTokenProvider.generateRefreshToken(username);

        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setToken(refreshToken);
        refreshTokenObj.setUserId(username);
        refreshTokenObj.setExpiresAt(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000L));
        refreshTokenService.saveRefreshToken(refreshTokenObj);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        response.sendRedirect("http://localhost:4200/home");
    }

}