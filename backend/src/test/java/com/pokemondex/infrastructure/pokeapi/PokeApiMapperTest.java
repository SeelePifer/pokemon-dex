package com.pokemondex.infrastructure.pokeapi;

import com.pokemondex.domain.model.PokemonDetail;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PokeApiMapperTest {

    @Test
    void flattensEvolutionAndCleansFlavorText() {
        PokeApiPayloads.Pokemon pokemon = new PokeApiPayloads.Pokemon(
                1,
                "bulbasaur",
                69,
                new PokeApiPayloads.Sprites(
                        "https://sprite/1.png",
                        new PokeApiPayloads.Other(new PokeApiPayloads.OfficialArtwork("https://art/1.png"))
                ),
                List.of(
                        new PokeApiPayloads.TypeSlot(2, new PokeApiPayloads.NamedResource("poison", "")),
                        new PokeApiPayloads.TypeSlot(1, new PokeApiPayloads.NamedResource("grass", ""))
                ),
                List.of(new PokeApiPayloads.AbilitySlot(new PokeApiPayloads.NamedResource("overgrow", ""), false)),
                List.of(new PokeApiPayloads.StatSlot(45, new PokeApiPayloads.NamedResource("hp", ""))),
                new PokeApiPayloads.NamedResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon-species/1/")
        );
        PokeApiPayloads.Species species = new PokeApiPayloads.Species(
                List.of(new PokeApiPayloads.FlavorText("A strange seed\nwas planted.", new PokeApiPayloads.NamedResource("en", ""))),
                new PokeApiPayloads.NamedResource("chain", "https://pokeapi.co/api/v2/evolution-chain/1/")
        );
        PokeApiPayloads.Evolution evolution = new PokeApiPayloads.Evolution(new PokeApiPayloads.ChainLink(
                new PokeApiPayloads.NamedResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon-species/1/"),
                List.of(new PokeApiPayloads.ChainLink(
                        new PokeApiPayloads.NamedResource("ivysaur", "https://pokeapi.co/api/v2/pokemon-species/2/"),
                        List.of(new PokeApiPayloads.ChainLink(
                                new PokeApiPayloads.NamedResource("venusaur", "https://pokeapi.co/api/v2/pokemon-species/3/"),
                                List.of()
                        ))
                ))
        ));

        PokemonDetail detail = PokeApiMapper.toDetail(pokemon, species, evolution);

        assertEquals(6.9, detail.massKilograms(), 0.001);
        assertEquals(List.of("grass", "poison"), detail.types());
        assertEquals("A strange seed was planted.", detail.description());
        assertEquals(List.of("bulbasaur", "ivysaur", "venusaur"), detail.evolutions().stream().map(stage -> stage.name()).toList());
        assertEquals("https://art/1.png", detail.imageUrl());
        assertTrue(detail.evolutions().get(2).spriteUrl().endsWith("/3.png"));
    }
}
