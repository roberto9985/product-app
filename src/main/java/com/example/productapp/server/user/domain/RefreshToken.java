package com.example.productapp.server.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Instant expiresAt;

    // indica daca tocken-ul a fost valid, chiar daca nu a expirat
    private boolean revoked = false;

    private Instant createdAt = Instant.now();

}