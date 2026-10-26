package com.pokemondex.infrastructure.pokeapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class PokeApiPayloads {

    private PokeApiPayloads() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NamedResource(String name, String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonList(long count, List<NamedResource> results) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Pokemon(
            int id,
            String name,
            int weight,
            Sprites sprites,
            List<TypeSlot> types,
            List<AbilitySlot> abilities,
            List<StatSlot> stats,
            NamedResource species
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sprites(
            @JsonProperty("front_default") String frontDefault,
            Other other
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Other(@JsonProperty("official-artwork") OfficialArtwork officialArtwork) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OfficialArtwork(@JsonProperty("front_default") String frontDefault) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TypeSlot(int slot, NamedResource type) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AbilitySlot(NamedResource ability, @JsonProperty("is_hidden") boolean hidden) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StatSlot(@JsonProperty("base_stat") int baseStat, NamedResource stat) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Species(
            @JsonProperty("flavor_text_entries") List<FlavorText> flavorTextEntries,
            @JsonProperty("evolution_chain") NamedResource evolutionChain
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FlavorText(@JsonProperty("flavor_text") String flavorText, NamedResource language) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Evolution(@JsonProperty("chain") ChainLink chain) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChainLink(NamedResource species, @JsonProperty("evolves_to") List<ChainLink> evolvesTo) {
    }
}
