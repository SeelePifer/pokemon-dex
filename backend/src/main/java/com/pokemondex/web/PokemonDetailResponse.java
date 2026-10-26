package com.pokemondex.web;

import java.util.List;

public record PokemonDetailResponse(
        int nationalId,
        String name,
        String imageUrl,
        String spriteUrl,
        List<String> types,
        double massKg,
        List<String> skills,
        List<StatResponse> stats,
        String description,
        List<EvolutionResponse> evolutions
) {

    public record StatResponse(String name, int value) {
    }

    public record EvolutionResponse(int nationalId, String name, String spriteUrl) {
    }
}
