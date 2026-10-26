package com.pokemondex.web;

import com.pokemondex.application.GetPokemonDetailUseCase;
import com.pokemondex.application.ListPokemonUseCase;
import com.pokemondex.domain.exception.ExternalServiceException;
import com.pokemondex.domain.exception.PokemonNotFoundException;
import com.pokemondex.domain.model.PageResult;
import com.pokemondex.domain.port.PokemonCatalogPort;
import com.pokemondex.infrastructure.config.JwtProperties;
import com.pokemondex.infrastructure.security.JwtTokenAdapter;
import com.pokemondex.infrastructure.security.SecurityConfig;
import com.pokemondex.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PokemonController.class, excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@Import({SecurityConfig.class, JwtTokenAdapter.class, ListPokemonUseCase.class, GetPokemonDetailUseCase.class})
@EnableConfigurationProperties(JwtProperties.class)
class PokemonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PokemonCatalogPort catalog;

    @Test
    void listReturnsSpriteCategoryMassAndSkills() throws Exception {
        when(catalog.list(any())).thenReturn(new PageResult<>(
                List.of(TestSupport.bulbasaur().toSummary()), 0, 12, 1
        ));

        mockMvc.perform(get("/api/v1/pokemon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("bulbasaur"))
                .andExpect(jsonPath("$.content[0].spriteUrl").exists())
                .andExpect(jsonPath("$.content[0].types[0]").value("grass"))
                .andExpect(jsonPath("$.content[0].massKg").value(6.9))
                .andExpect(jsonPath("$.content[0].skills[0]").value("overgrow"));
    }

    @Test
    void detailReturnsStatsDescriptionAndEvolutions() throws Exception {
        when(catalog.getByNameOrId("bulbasaur")).thenReturn(TestSupport.bulbasaur());

        mockMvc.perform(get("/api/v1/pokemon/bulbasaur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("https://img/1.png"))
                .andExpect(jsonPath("$.stats[0].name").value("hp"))
                .andExpect(jsonPath("$.description").value("A seeded description."))
                .andExpect(jsonPath("$.evolutions[0].name").value("bulbasaur"));
    }

    @Test
    void malformedNameReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/pokemon/bad name"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void missingPokemonReturns404() throws Exception {
        when(catalog.getByNameOrId("missingno")).thenThrow(new PokemonNotFoundException("missingno"));
        mockMvc.perform(get("/api/v1/pokemon/missingno"))
                .andExpect(status().isNotFound());
    }

    @Test
    void pokeApiFailureReturns502() throws Exception {
        when(catalog.getByNameOrId("bulbasaur")).thenThrow(new ExternalServiceException("PokeAPI is unavailable"));
        mockMvc.perform(get("/api/v1/pokemon/bulbasaur"))
                .andExpect(status().isBadGateway());
    }
}
