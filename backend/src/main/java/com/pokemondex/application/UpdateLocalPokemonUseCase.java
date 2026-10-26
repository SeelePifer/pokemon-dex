package com.pokemondex.application;

import com.pokemondex.domain.exception.LocalPokemonNotFoundException;
import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.ProprietaryFields;
import com.pokemondex.domain.port.LocalPokemonRepository;

import java.util.List;
import java.util.UUID;

public final class UpdateLocalPokemonUseCase {

    private final LocalPokemonRepository repository;

    public UpdateLocalPokemonUseCase(LocalPokemonRepository repository) {
        this.repository = repository;
    }

    public LocalPokemon execute(UUID id, Command command) {
        String localizedName = ProprietaryFields.requireText(
                command.localizedName(), "Localized name", ProprietaryFields.NAME_MAX
        );
        String region = ProprietaryFields.requireText(command.region(), "Region", ProprietaryFields.REGION_MAX);
        List<String> tags = ProprietaryFields.normalizeTags(command.tags());
        String notes = ProprietaryFields.notes(command.notes());
        LocalPokemon current = repository.findById(id).orElseThrow(() -> new LocalPokemonNotFoundException(id));
        return repository.save(current.withProprietary(localizedName, region, tags, notes));
    }

    public record Command(String localizedName, String region, List<String> tags, String notes) {
    }
}
