package com.pokemondex.domain.port;

import com.pokemondex.domain.model.UserAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<UserAccount> findById(UUID id);

    Optional<UserAccount> findByEmail(String email);

    List<UserAccount> findAll();

    UserAccount save(UserAccount user);

    void deleteById(UUID id);

    boolean existsByEmail(String email);
}
