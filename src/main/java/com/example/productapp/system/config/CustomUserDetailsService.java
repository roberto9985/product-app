package com.example.productapp.system.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String USER_USERNAME = "user";
    private static final String USER_PASSWORD = "user123";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (ADMIN_USERNAME.equals(username)) {
            return User.builder()
                    .username(ADMIN_USERNAME)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .authorities(ROLE_ADMIN)
                    .build();
        }

        if (USER_USERNAME.equals(username)) {
            return User.builder()
                    .username(USER_USERNAME)
                    .password(passwordEncoder.encode(USER_PASSWORD))
                    .authorities(ROLE_USER)
                    .build();
        }

        throw new UsernameNotFoundException("User not found");
    }
}
