package com.pokemondex.domain.port;

import com.pokemondex.domain.model.AuthToken;
import com.pokemondex.domain.model.AuthenticatedUser;
import com.pokemondex.domain.model.UserAccount;

import java.util.Optional;

public interface TokenIssuer {

    AuthToken issue(UserAccount user);

    Optional<AuthenticatedUser> parse(String token);
}
