package com.pokemondex.infrastructure.security;

import com.pokemondex.domain.port.PasswordHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    PasswordHasher passwordHasher(PasswordEncoder passwordEncoder) {
        return new BcryptPasswordHasher(passwordEncoder);
    }

    static final class BcryptPasswordHasher implements PasswordHasher {

        private final PasswordEncoder passwordEncoder;

        BcryptPasswordHasher(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public String hash(String rawPassword) {
            return passwordEncoder.encode(rawPassword);
        }

        @Override
        public boolean matches(String rawPassword, String passwordHash) {
            return passwordEncoder.matches(rawPassword, passwordHash);
        }
    }
}
