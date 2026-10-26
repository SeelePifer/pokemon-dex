package com.pokemondex.domain.model;

import com.pokemondex.domain.exception.InvalidRequestException;

public final class PokemonRef {

    private PokemonRef() {
    }

    public static String normalize(String nameOrId) {
        if (nameOrId == null || !nameOrId.matches("[A-Za-z0-9-]+")) {
            throw new InvalidRequestException(
                    "Pokemon id or name must contain only letters, numbers, and hyphens"
            );
        }
        return nameOrId.toLowerCase();
    }
}
