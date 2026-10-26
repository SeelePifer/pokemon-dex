package com.pokemondex.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateLocalPokemonRequest(
        @NotBlank @Size(max = 80) String localizedName,
        @NotBlank @Size(max = 60) String region,
        @NotNull @Size(max = 10) List<@NotBlank @Size(max = 30) @Pattern(regexp = "[a-z0-9-]{1,30}") String> tags,
        @Size(max = 500) String notes
) {
}
