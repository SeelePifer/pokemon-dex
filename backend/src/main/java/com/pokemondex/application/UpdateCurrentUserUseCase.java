package com.pokemondex.application;

import com.pokemondex.domain.exception.UserNotFoundException;
import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.UserRepository;

import java.util.UUID;

public final class UpdateCurrentUserUseCase {

    private final UserRepository users;

    public UpdateCurrentUserUseCase(UserRepository users) {
        this.users = users;
    }

    public UserAccount execute(UUID id, String displayName) {
        String validName = RegisterUserUseCase.requireDisplayName(displayName);
        UserAccount user = users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return users.save(user.withDisplayName(validName));
    }
}
