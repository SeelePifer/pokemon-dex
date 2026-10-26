package com.pokemondex.infrastructure.pokeapi;

import com.pokemondex.domain.model.PageQuery;
import com.pokemondex.domain.model.PageResult;
import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.model.PokemonSummary;
import com.pokemondex.domain.port.PokemonCatalogPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Component
public class PokemonCatalogAdapter implements PokemonCatalogPort {

    private final PokeApiClient client;
    private final Executor pokeApiExecutor;

    public PokemonCatalogAdapter(PokeApiClient client, @Qualifier("pokeApiExecutor") Executor pokeApiExecutor) {
        this.client = client;
        this.pokeApiExecutor = pokeApiExecutor;
    }

    @Override
    @Cacheable(cacheNames = "pokemonPages", key = "#query.page() + ':' + #query.size()")
    public PageResult<PokemonSummary> list(PageQuery query) {
        PokeApiPayloads.PokemonList list = client.list(query.offset(), query.size());
        List<String> names = list.results() == null
                ? List.of()
                : list.results().stream().map(PokeApiPayloads.NamedResource::name).toList();
        List<CompletableFuture<PokemonSummary>> futures = names.stream()
                .map(name -> CompletableFuture.supplyAsync(() -> client.getByNameOrId(name).toSummary(), pokeApiExecutor))
                .toList();
        List<PokemonSummary> content = futures.stream().map(PokemonCatalogAdapter::join).toList();
        return new PageResult<>(content, query.page(), query.size(), list.count());
    }

    @Override
    public PokemonDetail getByNameOrId(String nameOrId) {
        return client.getByNameOrId(nameOrId);
    }

    private static PokemonSummary join(CompletableFuture<PokemonSummary> future) {
        try {
            return future.join();
        } catch (CompletionException exception) {
            if (exception.getCause() instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw exception;
        }
    }
}
