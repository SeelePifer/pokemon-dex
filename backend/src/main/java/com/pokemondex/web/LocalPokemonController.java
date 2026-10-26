package com.pokemondex.web;

import com.pokemondex.application.DeleteLocalPokemonUseCase;
import com.pokemondex.application.GetLocalPokemonUseCase;
import com.pokemondex.application.ListLocalPokemonUseCase;
import com.pokemondex.application.SyncPokemonUseCase;
import com.pokemondex.application.UpdateLocalPokemonUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/local-pokemon")
public class LocalPokemonController {

    private final ListLocalPokemonUseCase listLocalPokemon;
    private final GetLocalPokemonUseCase getLocalPokemon;
    private final SyncPokemonUseCase syncPokemon;
    private final UpdateLocalPokemonUseCase updateLocalPokemon;
    private final DeleteLocalPokemonUseCase deleteLocalPokemon;

    public LocalPokemonController(
            ListLocalPokemonUseCase listLocalPokemon,
            GetLocalPokemonUseCase getLocalPokemon,
            SyncPokemonUseCase syncPokemon,
            UpdateLocalPokemonUseCase updateLocalPokemon,
            DeleteLocalPokemonUseCase deleteLocalPokemon
    ) {
        this.listLocalPokemon = listLocalPokemon;
        this.getLocalPokemon = getLocalPokemon;
        this.syncPokemon = syncPokemon;
        this.updateLocalPokemon = updateLocalPokemon;
        this.deleteLocalPokemon = deleteLocalPokemon;
    }

    @GetMapping
    public PageResponse<LocalPokemonResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(20) int size
    ) {
        return HttpMapper.localPage(listLocalPokemon.execute(page, size));
    }

    @GetMapping("/{id}")
    public LocalPokemonResponse get(@PathVariable UUID id) {
        return HttpMapper.local(getLocalPokemon.execute(id));
    }

    @PostMapping("/sync/{nameOrId}")
    public LocalPokemonResponse sync(@PathVariable String nameOrId) {
        return HttpMapper.local(syncPokemon.execute(nameOrId));
    }

    @PutMapping("/{id}")
    public LocalPokemonResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateLocalPokemonRequest request) {
        return HttpMapper.local(updateLocalPokemon.execute(id, new UpdateLocalPokemonUseCase.Command(
                request.localizedName(),
                request.region(),
                request.tags(),
                request.notes()
        )));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteLocalPokemon.execute(id);
        return ResponseEntity.noContent().build();
    }
}
