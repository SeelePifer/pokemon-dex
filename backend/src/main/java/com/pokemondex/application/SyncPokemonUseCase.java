package com.pokemondex.application;

import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.model.PokemonRef;
import com.pokemondex.domain.port.LocalPokemonRepository;
import com.pokemondex.domain.port.PokemonCatalogPort;

import java.util.List;
import java.util.Locale;

public final class SyncPokemonUseCase {

    private final PokemonCatalogPort catalog;
    private final LocalPokemonRepository repository;

    public SyncPokemonUseCase(PokemonCatalogPort catalog, LocalPokemonRepository repository) {
        this.catalog = catalog;
        this.repository = repository;
    }

    public LocalPokemon execute(String nameOrId) {
        PokemonDetail detail = catalog.getByNameOrId(PokemonRef.normalize(nameOrId));
        return repository.findByNationalId(detail.nationalId())
                .map(existing -> repository.save(existing.withSnapshot(detail)))
                .orElseGet(() -> repository.save(LocalPokemon.createFrom(
                        detail,
                        title(detail.name()),
                        "Unassigned",
                        List.of(),
                        ""
                )));
    }

    private static String title(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }
}
