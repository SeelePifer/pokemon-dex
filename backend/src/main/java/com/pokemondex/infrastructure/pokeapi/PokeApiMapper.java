package com.pokemondex.infrastructure.pokeapi;

import com.pokemondex.domain.model.EvolutionStage;
import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.model.PokemonStat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PokeApiMapper {

    private PokeApiMapper() {
    }

    public static PokemonDetail toDetail(
            PokeApiPayloads.Pokemon pokemon,
            PokeApiPayloads.Species species,
            PokeApiPayloads.Evolution evolution
    ) {
        String sprite = spriteFor(pokemon.id());
        String artwork = artwork(pokemon);
        return new PokemonDetail(
                pokemon.id(),
                pokemon.name(),
                artwork,
                sprite,
                types(pokemon),
                pokemon.weight(),
                skills(pokemon),
                stats(pokemon),
                description(species),
                evolutions(evolution)
        );
    }

    public static String spriteFor(int nationalId) {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + nationalId + ".png";
    }

    static String artwork(PokeApiPayloads.Pokemon pokemon) {
        if (pokemon.sprites() != null
                && pokemon.sprites().other() != null
                && pokemon.sprites().other().officialArtwork() != null
                && pokemon.sprites().other().officialArtwork().frontDefault() != null) {
            return pokemon.sprites().other().officialArtwork().frontDefault();
        }
        if (pokemon.sprites() != null && pokemon.sprites().frontDefault() != null) {
            return pokemon.sprites().frontDefault();
        }
        return spriteFor(pokemon.id());
    }

    static List<String> types(PokeApiPayloads.Pokemon pokemon) {
        if (pokemon.types() == null) {
            return List.of();
        }
        return pokemon.types().stream()
                .sorted(Comparator.comparingInt(PokeApiPayloads.TypeSlot::slot))
                .map(slot -> slot.type().name())
                .toList();
    }

    static List<String> skills(PokeApiPayloads.Pokemon pokemon) {
        if (pokemon.abilities() == null) {
            return List.of();
        }
        return pokemon.abilities().stream().map(slot -> slot.ability().name()).toList();
    }

    static List<PokemonStat> stats(PokeApiPayloads.Pokemon pokemon) {
        if (pokemon.stats() == null) {
            return List.of();
        }
        return pokemon.stats().stream()
                .map(slot -> new PokemonStat(slot.stat().name(), slot.baseStat()))
                .toList();
    }

    static String description(PokeApiPayloads.Species species) {
        if (species == null || species.flavorTextEntries() == null) {
            return "";
        }
        return species.flavorTextEntries().stream()
                .filter(entry -> entry.language() != null && "en".equals(entry.language().name()))
                .map(entry -> entry.flavorText() == null ? "" : entry.flavorText())
                .map(PokeApiMapper::cleanFlavorText)
                .filter(text -> !text.isBlank())
                .findFirst()
                .orElse("");
    }

    static String cleanFlavorText(String value) {
        return value.replace('\f', ' ').replace('\n', ' ').replaceAll(" +", " ").trim();
    }

    static List<EvolutionStage> evolutions(PokeApiPayloads.Evolution evolution) {
        if (evolution == null || evolution.chain() == null) {
            return List.of();
        }
        List<EvolutionStage> stages = new ArrayList<>();
        walk(evolution.chain(), stages);
        return List.copyOf(stages);
    }

    private static void walk(PokeApiPayloads.ChainLink link, List<EvolutionStage> stages) {
        if (link == null || link.species() == null) {
            return;
        }
        int id = idFromUrl(link.species().url());
        stages.add(new EvolutionStage(id, link.species().name(), spriteFor(id)));
        if (link.evolvesTo() == null) {
            return;
        }
        for (PokeApiPayloads.ChainLink next : link.evolvesTo()) {
            walk(next, stages);
        }
    }

    static int idFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return 0;
        }
        String trimmed = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        int slash = trimmed.lastIndexOf('/');
        return Integer.parseInt(trimmed.substring(slash + 1));
    }
}
