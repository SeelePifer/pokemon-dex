package com.pokemondex.application;

import com.pokemondex.domain.exception.InvalidRequestException;
import com.pokemondex.domain.exception.UserNotFoundException;
import com.pokemondex.domain.port.UserRepository;

import java.util.UUID;

public final class DeleteUserUseCase {

    private final UserRepository users;

    public DeleteUserUseCase(UserRepository users) {
        this.users = users;
    }

    public void execute(UUID actorId, UUID targetId) {
        if (actorId.equals(targetId)) {
            throw new InvalidRequestException("You cannot delete your own account");
        }
        if (users.findById(targetId).isEmpty()) {
            throw new UserNotFoundException(targetId);
        }
        users.deleteById(targetId);
    }
}
