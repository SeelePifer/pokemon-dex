package com.pokemondex.domain.model;

public record AuthToken(String accessToken, long expiresInSeconds) {
}
