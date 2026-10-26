package com.pokemondex.web;

import java.util.List;

public record PokemonSummaryResponse(
        int nationalId,
        String name,
        String spriteUrl,
        List<String> types,
        double massKg,
        List<String> skills
) {
}
