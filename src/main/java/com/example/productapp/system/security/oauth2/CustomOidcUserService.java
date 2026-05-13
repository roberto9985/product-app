package com.example.productapp.system.security.oauth2;

import com.example.productapp.server.user.domain.AppUser;
import com.example.productapp.server.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final AppUserRepository appUserRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {

        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);
        String email = oidcUser.getEmail();
        String sub = oidcUser.getSubject();

        AppUser user = appUserRepository.findByUsername(email)
                .orElseGet(() -> createNewUser(email, sub));

        return new DefaultOidcUser(
                List.of(new SimpleGrantedAuthority(user.getRole() != null ? user.getRole() : "ROLE_USER")),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
    }

    private AppUser createNewUser(String username, String oauthId) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword("oauth_pass"); // Dummy password since it's not used for OAuth2 logins
        user.setRole("ROLE_USER");
        user.setOauthId("google:" + oauthId);
        return appUserRepository.save(user);
    }
}
