package com.pokemondex.infrastructure.persistence;

import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.PageQuery;
import com.pokemondex.domain.model.PageResult;
import com.pokemondex.domain.port.LocalPokemonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class LocalPokemonPersistenceAdapter implements LocalPokemonRepository {

    private final LocalPokemonJpaRepository jpa;

    public LocalPokemonPersistenceAdapter(LocalPokemonJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocalPokemon> findById(UUID id) {
        return jpa.findById(id).map(LocalPokemonEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocalPokemon> findByNationalId(int nationalId) {
        return jpa.findByNationalId(nationalId).map(LocalPokemonEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<LocalPokemon> findAll(PageQuery query) {
        Page<LocalPokemonEntity> page = jpa.findAll(PageRequest.of(query.page(), query.size(), Sort.by("nationalId")));
        return new PageResult<>(
                page.getContent().stream().map(LocalPokemonEntity::toDomain).toList(),
                query.page(),
                query.size(),
                page.getTotalElements()
        );
    }

    @Override
    public LocalPokemon save(LocalPokemon pokemon) {
        LocalPokemonEntity entity = jpa.findById(pokemon.id()).orElseGet(LocalPokemonEntity::new);
        entity.apply(pokemon);
        return jpa.save(entity).toDomain();
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}
