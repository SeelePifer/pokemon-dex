package com.pokemondex.web;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorItem> fieldErrors
) {

    public record FieldErrorItem(String field, String message) {
    }

    public static ApiError of(int status, String error, String message, String path, List<FieldErrorItem> fieldErrors) {
        return new ApiError(
                Instant.now(),
                status,
                error,
                message,
                path,
                fieldErrors == null ? List.of() : fieldErrors
        );
    }
}
