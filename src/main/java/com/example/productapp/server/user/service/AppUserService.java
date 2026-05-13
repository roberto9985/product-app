package com.example.productapp.server.user.service;

import com.example.productapp.server.user.domain.AppUser;
import com.example.productapp.server.user.domain.RefreshToken;
import com.example.productapp.server.user.exceptions.UserAlreadyExistsException;
import com.example.productapp.server.user.repository.AppUserRepository;
import com.example.productapp.server.user.rest.dto.LoginRequest;
import com.example.productapp.server.user.rest.dto.LoginResponse;
import com.example.productapp.server.user.rest.dto.UserRegisterRequest;
import com.example.productapp.system.security.JwtService;
import com.example.productapp.system.security.LoginRateLimiter;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final LoginRateLimiter loginRateLimiter;

    @Transactional
    public void registerUser(UserRegisterRequest request) {
        if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        appUserRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

                UserDetails user = (UserDetails) authentication.getPrincipal();

                loginRateLimiter.resetUser(request.username());

                String accessToken = jwtService.generateAccessToken(user);
                String refreshTokenStr = jwtService.generateRefreshToken(user.getUsername());

                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setToken(refreshTokenStr);
                refreshToken.setUserId(user.getUsername());
                refreshToken.setExpiresAt(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000L));
                refreshTokenService.saveRefreshToken(refreshToken);

                return new LoginResponse(accessToken, refreshTokenStr);

        } catch (BadCredentialsException ex) {
            loginRateLimiter.consumeFailedAttempt(request.username());
            throw ex;
        }
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final Object principal = auth.getPrincipal();
        return 3L;
    }

    public boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

}
