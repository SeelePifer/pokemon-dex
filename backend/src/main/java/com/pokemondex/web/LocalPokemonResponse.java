package com.pokemondex.web;

import java.time.Instant;
import java.util.List;

public record LocalPokemonResponse(
        String id,
        int nationalId,
        String name,
        String spriteUrl,
        String imageUrl,
        List<String> types,
        double massKg,
        List<String> skills,
        List<PokemonDetailResponse.StatResponse> stats,
        String description,
        List<PokemonDetailResponse.EvolutionResponse> evolutions,
        String localizedName,
        String region,
        List<String> tags,
        String notes,
        Instant updatedAt
) {
}
