package com.pokemondex.infrastructure.pokeapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemondex.domain.exception.ExternalServiceException;
import com.pokemondex.domain.exception.PokemonNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class PokeApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PokeApiClient(RestClient pokeApiRestClient, ObjectMapper objectMapper) {
        this.restClient = pokeApiRestClient;
        this.objectMapper = objectMapper;
    }

    public PokeApiPayloads.PokemonList list(int offset, int limit) {
        return get(
                "/pokemon?limit=" + limit + "&offset=" + offset,
                PokeApiPayloads.PokemonList.class,
                "page"
        );
    }

    @Cacheable(cacheNames = "pokemonDetails", key = "#nameOrId")
    public com.pokemondex.domain.model.PokemonDetail getByNameOrId(String nameOrId) {
        PokeApiPayloads.Pokemon pokemon = get("/pokemon/" + nameOrId, PokeApiPayloads.Pokemon.class, nameOrId);
        PokeApiPayloads.Species species = get(pokemon.species().url(), PokeApiPayloads.Species.class, nameOrId);
        String evolutionUrl = species.evolutionChain() == null ? null : species.evolutionChain().url();
        PokeApiPayloads.Evolution evolution = evolutionUrl == null
                ? null
                : get(evolutionUrl, PokeApiPayloads.Evolution.class, nameOrId);
        return PokeApiMapper.toDetail(pokemon, species, evolution);
    }

    private <T> T get(String uri, Class<T> type, String nameOrId) {
        try {
            return restClient.get().uri(uri).exchange((request, response) -> {
                if (response.getStatusCode().value() == 404) {
                    throw new PokemonNotFoundException(nameOrId);
                }
                if (response.getStatusCode().isError()) {
                    throw new ExternalServiceException("PokeAPI returned " + response.getStatusCode().value());
                }
                return objectMapper.readValue(response.getBody(), type);
            });
        } catch (PokemonNotFoundException | ExternalServiceException exception) {
            throw exception;
        } catch (RestClientException exception) {
            Throwable cause = exception.getCause() == null ? exception : exception.getCause();
            if (cause instanceof PokemonNotFoundException notFound) {
                throw notFound;
            }
            if (cause instanceof ExternalServiceException external) {
                throw external;
            }
            throw new ExternalServiceException("PokeAPI is unavailable");
        }
    }
}
