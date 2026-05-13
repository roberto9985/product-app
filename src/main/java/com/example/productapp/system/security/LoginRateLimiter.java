package com.example.productapp.system.security;

import com.example.productapp.system.security.exceptions.TooManyLoginAttemptsException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    private Bucket createUserBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(20))))
                .build();
    }

    public void consumeFailedAttempt(String username) {
        Bucket bucket = userBuckets.computeIfAbsent(username.toLowerCase(), key -> createUserBucket());

        if (!bucket.tryConsume(1)) {
            throw new TooManyLoginAttemptsException("Too many failed login attempts for this user.");
        }
    }

    public void resetUser(String username) {
        userBuckets.remove(username.toLowerCase());
    }

}
