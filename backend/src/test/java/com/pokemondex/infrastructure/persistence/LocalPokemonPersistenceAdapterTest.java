package com.pokemondex.infrastructure.persistence;

import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.PageQuery;
import com.pokemondex.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@AutoConfigureTestDatabase
@Import(LocalPokemonPersistenceAdapter.class)
class LocalPokemonPersistenceAdapterTest {

    @Autowired
    LocalPokemonPersistenceAdapter adapter;

    @Test
    void savesAndReadsAPokemonByNationalId() {
        LocalPokemon saved = adapter.save(LocalPokemon.createFrom(
                TestSupport.bulbasaur(),
                "Bulbasaur",
                "Kanto",
                List.of("starter", "internal-grass"),
                "lab note"
        ));

        LocalPokemon found = adapter.findByNationalId(1).orElseThrow();
        assertEquals(saved.id(), found.id());
        assertEquals(List.of("starter", "internal-grass"), found.tags());
        assertEquals("lab note", found.notes());
        assertEquals(1, adapter.findAll(new PageQuery(0, 12)).totalElements());
        assertTrue(adapter.findById(saved.id()).isPresent());
    }
}
