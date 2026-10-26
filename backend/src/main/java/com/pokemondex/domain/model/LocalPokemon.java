package com.pokemondex.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LocalPokemon(
        UUID id,
        int nationalId,
        String name,
        String spriteUrl,
        String imageUrl,
        List<String> types,
        int weightHectograms,
        List<String> skills,
        List<PokemonStat> stats,
        String description,
        List<EvolutionStage> evolutions,
        String localizedName,
        String region,
        List<String> tags,
        String notes,
        Instant updatedAt
) {

    public double massKilograms() {
        return weightHectograms / 10.0;
    }

    public static LocalPokemon createFrom(
            PokemonDetail detail,
            String localizedName,
            String region,
            List<String> tags,
            String notes
    ) {
        return new LocalPokemon(
                UUID.randomUUID(),
                detail.nationalId(),
                detail.name(),
                detail.spriteUrl(),
                detail.imageUrl(),
                List.copyOf(detail.types()),
                detail.weightHectograms(),
                List.copyOf(detail.skills()),
                List.copyOf(detail.stats()),
                detail.description(),
                List.copyOf(detail.evolutions()),
                localizedName,
                region,
                List.copyOf(tags),
                notes == null ? "" : notes,
                Instant.now()
        );
    }

    public LocalPokemon withSnapshot(PokemonDetail detail) {
        return new LocalPokemon(
                id,
                detail.nationalId(),
                detail.name(),
                detail.spriteUrl(),
                detail.imageUrl(),
                List.copyOf(detail.types()),
                detail.weightHectograms(),
                List.copyOf(detail.skills()),
                List.copyOf(detail.stats()),
                detail.description(),
                List.copyOf(detail.evolutions()),
                localizedName,
                region,
                tags,
                notes,
                Instant.now()
        );
    }

    public LocalPokemon withProprietary(String localizedName, String region, List<String> tags, String notes) {
        return new LocalPokemon(
                id,
                nationalId,
                name,
                spriteUrl,
                imageUrl,
                types,
                weightHectograms,
                skills,
                stats,
                description,
                evolutions,
                localizedName,
                region,
                List.copyOf(tags),
                notes == null ? "" : notes,
                Instant.now()
        );
    }
}
