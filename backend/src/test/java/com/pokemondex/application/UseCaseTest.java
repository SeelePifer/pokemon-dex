package com.pokemondex.application;

import com.pokemondex.domain.exception.DuplicateEmailException;
import com.pokemondex.domain.exception.InvalidRequestException;
import com.pokemondex.domain.exception.LocalPokemonNotFoundException;
import com.pokemondex.domain.exception.PokemonNotFoundException;
import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.model.Role;
import com.pokemondex.support.TestSupport;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCaseTest {

    private final TestSupport.Catalog catalog = new TestSupport.Catalog().with(TestSupport.bulbasaur());
    private final TestSupport.LocalStore local = new TestSupport.LocalStore();
    private final TestSupport.Users users = new TestSupport.Users();

    @Test
    void listRejectsAnInvalidPageBeforeCallingTheCatalog() {
        ListPokemonUseCase useCase = new ListPokemonUseCase(catalog);
        assertThrows(InvalidRequestException.class, () -> useCase.execute(0, 50));
    }

    @Test
    void detailReturnsTheCatalogEntry() {
        GetPokemonDetailUseCase useCase = new GetPokemonDetailUseCase(catalog);
        assertEquals("bulbasaur", useCase.execute("Bulbasaur").name());
    }

    @Test
    void detailRejectsUnknownPokemon() {
        GetPokemonDetailUseCase useCase = new GetPokemonDetailUseCase(catalog);
        assertThrows(PokemonNotFoundException.class, () -> useCase.execute("missingno"));
    }

    @Test
    void syncKeepsProprietaryFieldsWhenTheSnapshotIsRefreshed() {
        SyncPokemonUseCase useCase = new SyncPokemonUseCase(catalog, local);
        LocalPokemon created = useCase.execute("bulbasaur");
        LocalPokemon edited = local.save(created.withProprietary("Bulbasaur", "Kanto", List.of("starter"), "keep me"));
        PokemonDetail heavier = TestSupport.detail(1, "bulbasaur", 100, List.of("grass"), List.of("overgrow"));
        catalog.with(heavier);

        LocalPokemon synced = useCase.execute("1");

        assertEquals(edited.id(), synced.id());
        assertEquals(100, synced.weightHectograms());
        assertEquals("Kanto", synced.region());
        assertEquals(List.of("starter"), synced.tags());
        assertEquals("keep me", synced.notes());
    }

    @Test
    void updateMissingPokemonReturnsNotFound() {
        UpdateLocalPokemonUseCase useCase = new UpdateLocalPokemonUseCase(local);
        UUID id = UUID.randomUUID();
        UpdateLocalPokemonUseCase.Command command = new UpdateLocalPokemonUseCase.Command(
                "Bulbasaur", "Kanto", List.of("starter"), "notes"
        );
        assertThrows(LocalPokemonNotFoundException.class, () -> useCase.execute(id, command));
    }

    @Test
    void updateRejectsABlankRegion() {
        LocalPokemon pokemon = LocalPokemon.createFrom(TestSupport.bulbasaur(), "Bulbasaur", "Kanto", List.of(), "");
        local.save(pokemon);
        UpdateLocalPokemonUseCase useCase = new UpdateLocalPokemonUseCase(local);
        UpdateLocalPokemonUseCase.Command command = new UpdateLocalPokemonUseCase.Command("Name", " ", List.of(), "");
        assertThrows(InvalidRequestException.class, () -> useCase.execute(pokemon.id(), command));
    }

    @Test
    void deleteMissingPokemonReturnsNotFound() {
        DeleteLocalPokemonUseCase useCase = new DeleteLocalPokemonUseCase(local);
        assertThrows(LocalPokemonNotFoundException.class, () -> useCase.execute(UUID.randomUUID()));
    }

    @Test
    void registerRejectsADuplicateEmail() {
        users.save(TestSupport.user(Role.USER));
        RegisterUserUseCase useCase = new RegisterUserUseCase(users, new TestSupport.Hasher());
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Trainer@pokemon.local", "secret123", "Trainer"
        );
        assertThrows(DuplicateEmailException.class, () -> useCase.execute(command));
    }

    @Test
    void loginRejectsAWrongPassword() {
        users.save(TestSupport.user(Role.USER));
        LoginUseCase useCase = new LoginUseCase(users, new TestSupport.Hasher(), new TestSupport.Tokens());
        assertThrows(com.pokemondex.domain.exception.AuthenticationFailedException.class,
                () -> useCase.execute("trainer@pokemon.local", "wrong-password"));
    }

    @Test
    void loginReturnsATokenForTheRightPassword() {
        users.save(TestSupport.user(Role.USER));
        LoginUseCase useCase = new LoginUseCase(users, new TestSupport.Hasher(), new TestSupport.Tokens());
        LoginUseCase.Result result = useCase.execute("trainer@pokemon.local", "secret123");
        assertEquals("token-trainer@pokemon.local", result.token().accessToken());
    }

    @Test
    void aUserCannotDeleteTheirOwnAccount() {
        var user = users.save(TestSupport.user(Role.ADMIN));
        DeleteUserUseCase useCase = new DeleteUserUseCase(users);
        assertThrows(InvalidRequestException.class, () -> useCase.execute(user.id(), user.id()));
        assertTrue(users.findById(user.id()).isPresent());
    }
}
