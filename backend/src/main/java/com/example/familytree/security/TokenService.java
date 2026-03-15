package com.example.familytree.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private final Map<String, String> tokenToUsername = new ConcurrentHashMap<>();

    public String issueToken(String username) {
        String token = UUID.randomUUID().toString();
        tokenToUsername.put(token, username);
        return token;
    }

    public Optional<String> getUsername(String token) {
        return Optional.ofNullable(tokenToUsername.get(token));
    }
}
