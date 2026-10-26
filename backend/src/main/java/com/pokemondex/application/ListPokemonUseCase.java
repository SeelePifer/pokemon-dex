package com.pokemondex.application;

import com.pokemondex.domain.model.PageQuery;
import com.pokemondex.domain.model.PageResult;
import com.pokemondex.domain.model.PokemonSummary;
import com.pokemondex.domain.port.PokemonCatalogPort;

public final class ListPokemonUseCase {

    private final PokemonCatalogPort catalog;

    public ListPokemonUseCase(PokemonCatalogPort catalog) {
        this.catalog = catalog;
    }

    public PageResult<PokemonSummary> execute(int page, int size) {
        return catalog.list(new PageQuery(page, size));
    }
}
