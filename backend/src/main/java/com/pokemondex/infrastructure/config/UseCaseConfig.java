package com.pokemondex.infrastructure.config;

import com.pokemondex.application.DeleteLocalPokemonUseCase;
import com.pokemondex.application.DeleteUserUseCase;
import com.pokemondex.application.GetCurrentUserUseCase;
import com.pokemondex.application.GetLocalPokemonUseCase;
import com.pokemondex.application.GetPokemonDetailUseCase;
import com.pokemondex.application.ListLocalPokemonUseCase;
import com.pokemondex.application.ListPokemonUseCase;
import com.pokemondex.application.ListUsersUseCase;
import com.pokemondex.application.LoginUseCase;
import com.pokemondex.application.RegisterUserUseCase;
import com.pokemondex.application.SyncPokemonUseCase;
import com.pokemondex.application.UpdateCurrentUserUseCase;
import com.pokemondex.application.UpdateLocalPokemonUseCase;
import com.pokemondex.domain.port.LocalPokemonRepository;
import com.pokemondex.domain.port.PasswordHasher;
import com.pokemondex.domain.port.PokemonCatalogPort;
import com.pokemondex.domain.port.TokenIssuer;
import com.pokemondex.domain.port.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    ListPokemonUseCase listPokemonUseCase(PokemonCatalogPort catalog) {
        return new ListPokemonUseCase(catalog);
    }

    @Bean
    GetPokemonDetailUseCase getPokemonDetailUseCase(PokemonCatalogPort catalog) {
        return new GetPokemonDetailUseCase(catalog);
    }

    @Bean
    SyncPokemonUseCase syncPokemonUseCase(PokemonCatalogPort catalog, LocalPokemonRepository repository) {
        return new SyncPokemonUseCase(catalog, repository);
    }

    @Bean
    ListLocalPokemonUseCase listLocalPokemonUseCase(LocalPokemonRepository repository) {
        return new ListLocalPokemonUseCase(repository);
    }

    @Bean
    GetLocalPokemonUseCase getLocalPokemonUseCase(LocalPokemonRepository repository) {
        return new GetLocalPokemonUseCase(repository);
    }

    @Bean
    UpdateLocalPokemonUseCase updateLocalPokemonUseCase(LocalPokemonRepository repository) {
        return new UpdateLocalPokemonUseCase(repository);
    }

    @Bean
    DeleteLocalPokemonUseCase deleteLocalPokemonUseCase(LocalPokemonRepository repository) {
        return new DeleteLocalPokemonUseCase(repository);
    }

    @Bean
    RegisterUserUseCase registerUserUseCase(UserRepository users, PasswordHasher hasher) {
        return new RegisterUserUseCase(users, hasher);
    }

    @Bean
    LoginUseCase loginUseCase(UserRepository users, PasswordHasher hasher, TokenIssuer tokens) {
        return new LoginUseCase(users, hasher, tokens);
    }

    @Bean
    GetCurrentUserUseCase getCurrentUserUseCase(UserRepository users) {
        return new GetCurrentUserUseCase(users);
    }

    @Bean
    UpdateCurrentUserUseCase updateCurrentUserUseCase(UserRepository users) {
        return new UpdateCurrentUserUseCase(users);
    }

    @Bean
    ListUsersUseCase listUsersUseCase(UserRepository users) {
        return new ListUsersUseCase(users);
    }

    @Bean
    DeleteUserUseCase deleteUserUseCase(UserRepository users) {
        return new DeleteUserUseCase(users);
    }
}
