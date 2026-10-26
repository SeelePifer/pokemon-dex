package com.pokemondex.web;

import com.pokemondex.application.DeleteLocalPokemonUseCase;
import com.pokemondex.application.GetLocalPokemonUseCase;
import com.pokemondex.application.ListLocalPokemonUseCase;
import com.pokemondex.application.SyncPokemonUseCase;
import com.pokemondex.application.UpdateLocalPokemonUseCase;
import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.Role;
import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.LocalPokemonRepository;
import com.pokemondex.domain.port.PokemonCatalogPort;
import com.pokemondex.domain.port.TokenIssuer;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LocalPokemonController.class, excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@Import({
        SecurityConfig.class,
        JwtTokenAdapter.class,
        ListLocalPokemonUseCase.class,
        GetLocalPokemonUseCase.class,
        SyncPokemonUseCase.class,
        UpdateLocalPokemonUseCase.class,
        DeleteLocalPokemonUseCase.class
})
@EnableConfigurationProperties(JwtProperties.class)
class LocalPokemonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TokenIssuer tokens;

    @MockitoBean
    LocalPokemonRepository repository;

    @MockitoBean
    PokemonCatalogPort catalog;

    @Test
    void syncWithoutATokenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/local-pokemon/sync/bulbasaur"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void updateMissingRecordReturns404() throws Exception {
        when(repository.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/v1/local-pokemon/" + UUID.randomUUID())
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"localizedName":"Bulbasaur","region":"Kanto","tags":["starter"],"notes":"ok"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void malformedUpdateReturns400() throws Exception {
        mockMvc.perform(put("/api/v1/local-pokemon/" + UUID.randomUUID())
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"localizedName":"","region":"","tags":["Nope"],"notes":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
    }

    @Test
    void invalidIdReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/local-pokemon/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticatedSyncReturns200() throws Exception {
        when(catalog.getByNameOrId("bulbasaur")).thenReturn(TestSupport.bulbasaur());
        when(repository.findByNationalId(1)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/v1/local-pokemon/sync/bulbasaur").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("Unassigned"))
                .andExpect(jsonPath("$.massKg").value(6.9));
    }

    @Test
    void deleteReturns204() throws Exception {
        LocalPokemon pokemon = LocalPokemon.createFrom(TestSupport.bulbasaur(), "Bulbasaur", "Kanto", java.util.List.of(), "");
        when(repository.findById(pokemon.id())).thenReturn(Optional.of(pokemon));
        mockMvc.perform(delete("/api/v1/local-pokemon/" + pokemon.id()).header("Authorization", bearer()))
                .andExpect(status().isNoContent());
    }

    private String bearer() {
        UserAccount user = UserAccount.create("demo@pokemon.local", "Demo", "hash", Role.USER);
        return "Bearer " + tokens.issue(user).accessToken();
    }
}
