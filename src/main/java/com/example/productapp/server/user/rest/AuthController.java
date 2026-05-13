package com.example.productapp.server.user.rest;

import com.example.productapp.server.user.exceptions.UserAlreadyExistsException;
import com.example.productapp.server.user.rest.dto.*;
import com.example.productapp.server.user.service.AppUserService;
import com.example.productapp.server.user.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AppUserService appUserService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest request) throws BadCredentialsException {

        LoginResponse response = appUserService.login(request);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", response.refreshToken())
                .httpOnly(true)
                .secure(false) // localhost -> HTTPS nu e activ
                .sameSite("Lax") // mai permisiv pentru localhost
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AccessTokenResponse(response.accessToken()));

    }


    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @CookieValue("refreshToken") String refreshToken
    ) {
        String newRefreshTokenStr = refreshTokenService
                .revokeAndGenerateRefreshToken(refreshToken)
                .getToken();

        String accessToken = refreshTokenService.generateNewAccessToken(newRefreshTokenStr);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshTokenStr)
                .httpOnly(true)
                .secure(false) // localhost -> HTTPS nu e activ
                .sameSite("Lax") // mai permisiv pentru localhost
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AccessTokenResponse(accessToken));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue("refreshToken") String refreshTokenStr) {
        refreshTokenService.logoutUser(refreshTokenStr);

        ResponseCookie deleteCookie  = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // localhost -> HTTPS nu e activ
                .sameSite("Lax") // mai permisiv pentru localhost
                .path("/api/auth/refresh")
                .maxAge(0)
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }


    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) throws UserAlreadyExistsException {
        appUserService.registerUser(request);
        return ResponseEntity.ok(new UserRegisterResponse("User registered successfully"));
    }
}