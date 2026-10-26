package com.pokemondex.application;

import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.UserRepository;

import java.util.List;

public final class ListUsersUseCase {

    private final UserRepository users;

    public ListUsersUseCase(UserRepository users) {
        this.users = users;
    }

    public List<UserAccount> execute() {
        return users.findAll();
    }
}
