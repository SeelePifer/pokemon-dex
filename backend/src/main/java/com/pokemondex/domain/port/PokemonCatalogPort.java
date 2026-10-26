package com.pokemondex.domain.port;

import com.pokemondex.domain.model.PageQuery;
import com.pokemondex.domain.model.PageResult;
import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.model.PokemonSummary;

public interface PokemonCatalogPort {

    PageResult<PokemonSummary> list(PageQuery query);

    PokemonDetail getByNameOrId(String nameOrId);
}
