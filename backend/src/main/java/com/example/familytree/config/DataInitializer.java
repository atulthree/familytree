package com.example.familytree.config;

import com.example.familytree.model.UserAccount;
import com.example.familytree.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedUser(UserAccountRepository userAccountRepository, PasswordEncoder encoder) {
        return args -> userAccountRepository.findByUsername("admin").orElseGet(() -> {
            UserAccount user = new UserAccount();
            user.setUsername("admin");
            user.setPassword(encoder.encode("admin123"));
            return userAccountRepository.save(user);
        });
    }
}
