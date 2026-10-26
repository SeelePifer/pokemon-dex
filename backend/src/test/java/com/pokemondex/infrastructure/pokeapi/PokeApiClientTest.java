package com.pokemondex.infrastructure.pokeapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemondex.domain.exception.PokemonNotFoundException;
import com.pokemondex.domain.model.PageQuery;
import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.port.PokemonCatalogPort;
import com.pokemondex.infrastructure.config.CacheConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;

@SpringJUnitConfig(PokeApiClientTest.Config.class)
class PokeApiClientTest {

    @Autowired
    PokeApiClient client;

    @Autowired
    PokemonCatalogPort catalog;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void reset() {
        server.reset();
        cacheManager.getCacheNames().forEach(name -> {
            if (cacheManager.getCache(name) != null) {
                cacheManager.getCache(name).clear();
            }
        });
    }

    @Test
    void unknownPokemonReturnsNotFound() {
        server.expect(requestTo(containsString("/pokemon/missingno"))).andRespond(withResourceNotFound());
        assertThrows(PokemonNotFoundException.class, () -> client.getByNameOrId("missingno"));
    }

    @Test
    void detailAndPageResponsesAreCached() {
        expectBulbasaur();
        PokemonDetail first = client.getByNameOrId("bulbasaur");
        PokemonDetail second = client.getByNameOrId("bulbasaur");
        server.verify();
        assertEquals(first, second);
        assertEquals("A strange seed was planted.", first.description());

        server.reset();
        expectList();
        catalog.list(new PageQuery(0, 1));
        catalog.list(new PageQuery(0, 1));
        server.verify();
    }

    private void expectList() {
        server.expect(requestTo(containsString("/pokemon?limit=1&offset=0")))
                .andRespond(withSuccess("""
                        {"count":1,"results":[{"name":"bulbasaur","url":"https://pokeapi.co/api/v2/pokemon/1/"}]}
                        """, MediaType.APPLICATION_JSON));
    }

    private void expectBulbasaur() {
        server.expect(requestTo(containsString("/pokemon/bulbasaur")))
                .andRespond(withSuccess(POKEMON, MediaType.APPLICATION_JSON));
        server.expect(requestTo(containsString("/pokemon-species/1")))
                .andRespond(withSuccess(SPECIES, MediaType.APPLICATION_JSON));
        server.expect(requestTo(containsString("/evolution-chain/1")))
                .andRespond(withSuccess(EVOLUTION, MediaType.APPLICATION_JSON));
    }

    @Configuration
    @EnableCaching
    @Import({CacheConfig.class, PokeApiClient.class, PokemonCatalogAdapter.class})
    static class Config {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        RestClient.Builder pokeApiRestClientBuilder() {
            return RestClient.builder().baseUrl("https://pokeapi.co/api/v2");
        }

        @Bean
        MockRestServiceServer mockRestServiceServer(RestClient.Builder pokeApiRestClientBuilder) {
            return MockRestServiceServer.bindTo(pokeApiRestClientBuilder).build();
        }

        @Bean
        RestClient pokeApiRestClient(RestClient.Builder pokeApiRestClientBuilder, MockRestServiceServer server) {
            return pokeApiRestClientBuilder.build();
        }

        @Bean(name = "pokeApiExecutor")
        @Qualifier("pokeApiExecutor")
        Executor pokeApiExecutor() {
            return Runnable::run;
        }
    }

    private static final String POKEMON = """
            {"id":1,"name":"bulbasaur","weight":69,
            "sprites":{"front_default":"https://sprite/1.png","other":{"official-artwork":{"front_default":"https://art/1.png"}}},
            "types":[{"slot":1,"type":{"name":"grass"}}],
            "abilities":[{"ability":{"name":"overgrow"},"is_hidden":false}],
            "stats":[{"base_stat":45,"stat":{"name":"hp"}}],
            "species":{"name":"bulbasaur","url":"https://pokeapi.co/api/v2/pokemon-species/1/"}}
            """;

    private static final String SPECIES = """
            {"flavor_text_entries":[{"flavor_text":"A strange seed\\nwas planted.","language":{"name":"en"}}],
            "evolution_chain":{"url":"https://pokeapi.co/api/v2/evolution-chain/1/"}}
            """;

    private static final String EVOLUTION = """
            {"chain":{"species":{"name":"bulbasaur","url":"https://pokeapi.co/api/v2/pokemon-species/1/"},"evolves_to":[]}}
            """;
}
