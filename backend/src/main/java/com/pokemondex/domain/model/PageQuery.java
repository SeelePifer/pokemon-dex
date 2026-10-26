package com.pokemondex.domain.model;

import com.pokemondex.domain.exception.InvalidRequestException;

public record PageQuery(int page, int size) {

    public static final int DEFAULT_SIZE = 12;
    public static final int MAX_SIZE = 20;

    public PageQuery {
        if (page < 0 || size < 1 || size > MAX_SIZE) {
            throw new InvalidRequestException(
                    "Page must be zero or greater and size must be between 1 and " + MAX_SIZE
            );
        }
    }

    public int offset() {
        return page * size;
    }
}
