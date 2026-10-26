package com.pokemondex.application;

import com.pokemondex.domain.exception.DuplicateEmailException;
import com.pokemondex.domain.exception.InvalidRequestException;
import com.pokemondex.domain.model.Role;
import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.PasswordHasher;
import com.pokemondex.domain.port.UserRepository;

public final class RegisterUserUseCase {

    private final UserRepository users;
    private final PasswordHasher hasher;

    public RegisterUserUseCase(UserRepository users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    public UserAccount execute(Command command) {
        String email = normalizeEmail(command.email());
        if (command.password() == null || command.password().length() < 8 || command.password().length() > 100) {
            throw new InvalidRequestException("Password must be between 8 and 100 characters");
        }
        String displayName = requireDisplayName(command.displayName());
        if (users.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        return users.save(UserAccount.create(email, displayName, hasher.hash(command.password()), Role.USER));
    }

    public record Command(String email, String password, String displayName) {
    }

    static String normalizeEmail(String email) {
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new InvalidRequestException("Email is invalid");
        }
        return email.trim().toLowerCase();
    }

    static String requireDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank() || displayName.trim().length() > 80) {
            throw new InvalidRequestException("Display name is required and must be at most 80 characters");
        }
        return displayName.trim();
    }
}
