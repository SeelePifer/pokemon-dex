package com.pokemondex.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pokemondex.pokeapi")
public record PokeApiProperties(String baseUrl, int parallelism) {
}
