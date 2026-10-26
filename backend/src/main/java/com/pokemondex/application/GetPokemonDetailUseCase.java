package com.pokemondex.application;

import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.model.PokemonRef;
import com.pokemondex.domain.port.PokemonCatalogPort;

public final class GetPokemonDetailUseCase {

    private final PokemonCatalogPort catalog;

    public GetPokemonDetailUseCase(PokemonCatalogPort catalog) {
        this.catalog = catalog;
    }

    public PokemonDetail execute(String nameOrId) {
        return catalog.getByNameOrId(PokemonRef.normalize(nameOrId));
    }
}
