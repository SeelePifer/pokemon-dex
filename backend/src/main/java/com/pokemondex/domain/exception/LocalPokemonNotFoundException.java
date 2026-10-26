package com.pokemondex.domain.exception;

import java.util.UUID;

public class LocalPokemonNotFoundException extends RuntimeException {

    public LocalPokemonNotFoundException(UUID id) {
        super("Local Pokemon not found: " + id);
    }
}
