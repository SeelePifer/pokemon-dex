package com.pokemondex.application;

import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.PageQuery;
import com.pokemondex.domain.model.PageResult;
import com.pokemondex.domain.port.LocalPokemonRepository;

public final class ListLocalPokemonUseCase {

    private final LocalPokemonRepository repository;

    public ListLocalPokemonUseCase(LocalPokemonRepository repository) {
        this.repository = repository;
    }

    public PageResult<LocalPokemon> execute(int page, int size) {
        return repository.findAll(new PageQuery(page, size));
    }
}
