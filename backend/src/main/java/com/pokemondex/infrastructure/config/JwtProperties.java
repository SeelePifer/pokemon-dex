package com.pokemondex.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pokemondex.jwt")
public record JwtProperties(String secret, long expirationMinutes) {
}
