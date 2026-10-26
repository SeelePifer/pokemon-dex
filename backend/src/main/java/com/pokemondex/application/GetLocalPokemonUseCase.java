package com.pokemondex.application;

import com.pokemondex.domain.exception.LocalPokemonNotFoundException;
import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.port.LocalPokemonRepository;

import java.util.UUID;

public final class GetLocalPokemonUseCase {

    private final LocalPokemonRepository repository;

    public GetLocalPokemonUseCase(LocalPokemonRepository repository) {
        this.repository = repository;
    }

    public LocalPokemon execute(UUID id) {
        return repository.findById(id).orElseThrow(() -> new LocalPokemonNotFoundException(id));
    }
}
