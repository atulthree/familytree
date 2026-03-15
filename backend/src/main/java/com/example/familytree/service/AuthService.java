package com.example.familytree.service;

import com.example.familytree.dto.LoginRequest;
import com.example.familytree.dto.LoginResponse;
import com.example.familytree.repository.UserAccountRepository;
import com.example.familytree.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserAccountRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserAccountRepository userRepo, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest request) {
        var user = userRepo.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        String token = tokenService.issueToken(user.getUsername());
        return new LoginResponse(token, user.getUsername());
    }
}
