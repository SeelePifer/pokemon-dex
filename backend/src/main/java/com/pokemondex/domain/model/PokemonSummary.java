package com.pokemondex.domain.model;

import java.util.List;

public record PokemonSummary(
        int nationalId,
        String name,
        String spriteUrl,
        List<String> types,
        int weightHectograms,
        List<String> skills
) {

    public double massKilograms() {
        return weightHectograms / 10.0;
    }
}
