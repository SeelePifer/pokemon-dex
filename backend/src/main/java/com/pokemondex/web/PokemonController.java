package com.pokemondex.web;

import com.pokemondex.application.GetPokemonDetailUseCase;
import com.pokemondex.application.ListPokemonUseCase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/pokemon")
public class PokemonController {

    private final ListPokemonUseCase listPokemon;
    private final GetPokemonDetailUseCase getPokemon;

    public PokemonController(ListPokemonUseCase listPokemon, GetPokemonDetailUseCase getPokemon) {
        this.listPokemon = listPokemon;
        this.getPokemon = getPokemon;
    }

    @GetMapping
    public PageResponse<PokemonSummaryResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(20) int size
    ) {
        return HttpMapper.summaries(listPokemon.execute(page, size));
    }

    @GetMapping("/{nameOrId}")
    public PokemonDetailResponse detail(@PathVariable String nameOrId) {
        return HttpMapper.detail(getPokemon.execute(nameOrId));
    }
}
