package com.pokemondex.application;

import com.pokemondex.domain.exception.UserNotFoundException;
import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.UserRepository;

import java.util.UUID;

public final class GetCurrentUserUseCase {

    private final UserRepository users;

    public GetCurrentUserUseCase(UserRepository users) {
        this.users = users;
    }

    public UserAccount execute(UUID id) {
        return users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
