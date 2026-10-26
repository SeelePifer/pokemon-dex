package com.pokemondex.infrastructure.persistence;

import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    public UserPersistenceAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> findByEmail(String email) {
        return jpa.findByEmail(email).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAccount> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public UserAccount save(UserAccount user) {
        UserEntity entity = jpa.findById(user.id()).orElseGet(UserEntity::new);
        entity.apply(user.id(), user.email(), user.displayName(), user.passwordHash(), user.role());
        UserEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    private UserAccount toDomain(UserEntity entity) {
        return new UserAccount(
                entity.getId(),
                entity.getEmail(),
                entity.getDisplayName(),
                entity.getPasswordHash(),
                entity.getRole()
        );
    }
}
