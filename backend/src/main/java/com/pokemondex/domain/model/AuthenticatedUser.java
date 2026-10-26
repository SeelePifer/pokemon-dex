package com.pokemondex.domain.model;

import java.util.UUID;

public record AuthenticatedUser(UUID id, String email, Role role) {
}
