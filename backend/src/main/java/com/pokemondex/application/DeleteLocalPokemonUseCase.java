package com.pokemondex.application;

import com.pokemondex.domain.exception.LocalPokemonNotFoundException;
import com.pokemondex.domain.port.LocalPokemonRepository;

import java.util.UUID;

public final class DeleteLocalPokemonUseCase {

    private final LocalPokemonRepository repository;

    public DeleteLocalPokemonUseCase(LocalPokemonRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        if (repository.findById(id).isEmpty()) {
            throw new LocalPokemonNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
