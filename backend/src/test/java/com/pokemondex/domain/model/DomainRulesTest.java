package com.pokemondex.domain.model;

import com.pokemondex.domain.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainRulesTest {

    @Test
    void massIsExposedInKilograms() {
        PokemonSummary summary = new PokemonSummary(1, "bulbasaur", "sprite", List.of("grass"), 69, List.of("overgrow"));
        assertEquals(6.9, summary.massKilograms(), 0.001);
    }

    @Test
    void pageQueryRejectsOutOfRangeValues() {
        assertThrows(InvalidRequestException.class, () -> new PageQuery(-1, 12));
        assertThrows(InvalidRequestException.class, () -> new PageQuery(0, 0));
        assertThrows(InvalidRequestException.class, () -> new PageQuery(0, 21));
        assertEquals(24, new PageQuery(2, 12).offset());
    }

    @Test
    void pokemonRefNormalizesNames() {
        assertEquals("mr-mime", PokemonRef.normalize("Mr-Mime"));
        assertThrows(InvalidRequestException.class, () -> PokemonRef.normalize("bad name"));
    }

    @Test
    void tagsAreNormalizedAndRejectedWhenMalformed() {
        assertEquals(List.of("starter", "internal-grass"), ProprietaryFields.normalizeTags(List.of("starter", "starter", "internal-grass")));
        assertThrows(InvalidRequestException.class, () -> ProprietaryFields.normalizeTags(List.of("Starter")));
        assertThrows(InvalidRequestException.class, () -> ProprietaryFields.requireText("  ", "Region", 60));
    }
}
