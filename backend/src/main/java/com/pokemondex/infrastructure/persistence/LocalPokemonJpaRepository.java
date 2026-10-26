package com.pokemondex.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocalPokemonJpaRepository extends JpaRepository<LocalPokemonEntity, UUID> {

    Optional<LocalPokemonEntity> findByNationalId(int nationalId);
}
