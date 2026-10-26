package com.pokemondex.domain.model;

import java.util.UUID;

public record UserAccount(
        UUID id,
        String email,
        String displayName,
        String passwordHash,
        Role role
) {

    public static UserAccount create(String email, String displayName, String passwordHash, Role role) {
        return new UserAccount(UUID.randomUUID(), email.toLowerCase(), displayName, passwordHash, role);
    }

    public UserAccount withDisplayName(String displayName) {
        return new UserAccount(id, email, displayName, passwordHash, role);
    }
}
