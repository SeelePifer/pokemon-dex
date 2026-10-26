package com.pokemondex.domain.model;

import java.util.List;

public record PokemonDetail(
        int nationalId,
        String name,
        String imageUrl,
        String spriteUrl,
        List<String> types,
        int weightHectograms,
        List<String> skills,
        List<PokemonStat> stats,
        String description,
        List<EvolutionStage> evolutions
) {

    public double massKilograms() {
        return weightHectograms / 10.0;
    }

    public PokemonSummary toSummary() {
        return new PokemonSummary(nationalId, name, spriteUrl, types, weightHectograms, skills);
    }
}
