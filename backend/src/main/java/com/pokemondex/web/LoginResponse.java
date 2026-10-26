package com.pokemondex.web;

public record LoginResponse(String accessToken, long expiresInSeconds, UserResponse user) {
}
