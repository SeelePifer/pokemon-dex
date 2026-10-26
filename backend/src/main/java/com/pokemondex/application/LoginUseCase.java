package com.pokemondex.application;

import com.pokemondex.domain.exception.AuthenticationFailedException;
import com.pokemondex.domain.model.AuthToken;
import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.PasswordHasher;
import com.pokemondex.domain.port.TokenIssuer;
import com.pokemondex.domain.port.UserRepository;

public final class LoginUseCase {

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final TokenIssuer tokens;

    public LoginUseCase(UserRepository users, PasswordHasher hasher, TokenIssuer tokens) {
        this.users = users;
        this.hasher = hasher;
        this.tokens = tokens;
    }

    public Result execute(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            throw new AuthenticationFailedException();
        }
        UserAccount user = users.findByEmail(email.trim().toLowerCase())
                .orElseThrow(AuthenticationFailedException::new);
        if (!hasher.matches(password, user.passwordHash())) {
            throw new AuthenticationFailedException();
        }
        return new Result(tokens.issue(user), user);
    }

    public record Result(AuthToken token, UserAccount user) {
    }
}
