package com.pokemondex.web;

import com.pokemondex.domain.model.EvolutionStage;
import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.PageResult;
import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.model.PokemonStat;
import com.pokemondex.domain.model.PokemonSummary;
import com.pokemondex.domain.model.UserAccount;

import java.util.List;

public final class HttpMapper {

    private HttpMapper() {
    }

    public static PageResponse<PokemonSummaryResponse> summaries(PageResult<PokemonSummary> page) {
        return new PageResponse<>(
                page.content().stream().map(HttpMapper::summary).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages()
        );
    }

    public static PokemonSummaryResponse summary(PokemonSummary pokemon) {
        return new PokemonSummaryResponse(
                pokemon.nationalId(),
                pokemon.name(),
                pokemon.spriteUrl(),
                pokemon.types(),
                pokemon.massKilograms(),
                pokemon.skills()
        );
    }

    public static PokemonDetailResponse detail(PokemonDetail pokemon) {
        return new PokemonDetailResponse(
                pokemon.nationalId(),
                pokemon.name(),
                pokemon.imageUrl(),
                pokemon.spriteUrl(),
                pokemon.types(),
                pokemon.massKilograms(),
                pokemon.skills(),
                stats(pokemon.stats()),
                pokemon.description(),
                evolutions(pokemon.evolutions())
        );
    }

    public static PageResponse<LocalPokemonResponse> localPage(PageResult<LocalPokemon> page) {
        return new PageResponse<>(
                page.content().stream().map(HttpMapper::local).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages()
        );
    }

    public static LocalPokemonResponse local(LocalPokemon pokemon) {
        return new LocalPokemonResponse(
                pokemon.id().toString(),
                pokemon.nationalId(),
                pokemon.name(),
                pokemon.spriteUrl(),
                pokemon.imageUrl(),
                pokemon.types(),
                pokemon.massKilograms(),
                pokemon.skills(),
                stats(pokemon.stats()),
                pokemon.description(),
                evolutions(pokemon.evolutions()),
                pokemon.localizedName(),
                pokemon.region(),
                pokemon.tags(),
                pokemon.notes(),
                pokemon.updatedAt()
        );
    }

    public static UserResponse user(UserAccount user) {
        return new UserResponse(user.id().toString(), user.email(), user.displayName(), user.role().name());
    }

    private static List<PokemonDetailResponse.StatResponse> stats(List<PokemonStat> stats) {
        return stats.stream().map(stat -> new PokemonDetailResponse.StatResponse(stat.name(), stat.value())).toList();
    }

    private static List<PokemonDetailResponse.EvolutionResponse> evolutions(List<EvolutionStage> stages) {
        return stages.stream()
                .map(stage -> new PokemonDetailResponse.EvolutionResponse(stage.nationalId(), stage.name(), stage.spriteUrl()))
                .toList();
    }
}
