package com.pokemondex.infrastructure.security;

import com.pokemondex.domain.model.Role;
import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.PasswordHasher;
import com.pokemondex.domain.port.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final UserRepository users;
    private final PasswordHasher hasher;

    public DemoDataSeeder(UserRepository users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    @Override
    public void run(ApplicationArguments args) {
        seed("demo@pokemon.local", "Demo1234!", "Demo Trainer", Role.USER);
        seed("admin@pokemon.local", "Admin1234!", "Demo Admin", Role.ADMIN);
    }

    private void seed(String email, String password, String displayName, Role role) {
        if (users.existsByEmail(email)) {
            return;
        }
        users.save(UserAccount.create(email, displayName, hasher.hash(password), role));
        log.info("Seeded demo user {}", email);
    }
}
