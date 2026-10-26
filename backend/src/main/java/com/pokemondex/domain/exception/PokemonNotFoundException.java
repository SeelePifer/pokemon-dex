package com.pokemondex.domain.exception;

public class PokemonNotFoundException extends RuntimeException {

    public PokemonNotFoundException(String nameOrId) {
        super("Pokemon not found: " + nameOrId);
    }
}
