package com.pokemondex.domain.port;

import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.PageQuery;
import com.pokemondex.domain.model.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface LocalPokemonRepository {

    Optional<LocalPokemon> findById(UUID id);

    Optional<LocalPokemon> findByNationalId(int nationalId);

    PageResult<LocalPokemon> findAll(PageQuery query);

    LocalPokemon save(LocalPokemon pokemon);

    void deleteById(UUID id);
}
