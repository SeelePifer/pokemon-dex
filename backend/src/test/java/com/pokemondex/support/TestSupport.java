package com.pokemondex.support;

import com.pokemondex.domain.model.EvolutionStage;
import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.PageQuery;
import com.pokemondex.domain.model.PageResult;
import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.model.PokemonStat;
import com.pokemondex.domain.model.PokemonSummary;
import com.pokemondex.domain.model.Role;
import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.LocalPokemonRepository;
import com.pokemondex.domain.port.PasswordHasher;
import com.pokemondex.domain.port.PokemonCatalogPort;
import com.pokemondex.domain.port.TokenIssuer;
import com.pokemondex.domain.port.UserRepository;
import com.pokemondex.domain.exception.PokemonNotFoundException;
import com.pokemondex.domain.model.AuthToken;
import com.pokemondex.domain.model.AuthenticatedUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class TestSupport {

    private TestSupport() {
    }

    public static PokemonDetail bulbasaur() {
        return detail(1, "bulbasaur", 69, List.of("grass", "poison"), List.of("overgrow"));
    }

    public static PokemonDetail pikachu() {
        return detail(25, "pikachu", 60, List.of("electric"), List.of("static"));
    }

    public static PokemonDetail detail(int id, String name, int weight, List<String> types, List<String> skills) {
        return new PokemonDetail(
                id,
                name,
                "https://img/" + id + ".png",
                "https://sprite/" + id + ".png",
                types,
                weight,
                skills,
                List.of(new PokemonStat("hp", 35)),
                "A seeded description.",
                List.of(new EvolutionStage(id, name, "https://sprite/" + id + ".png"))
        );
    }

    public static final class Catalog implements PokemonCatalogPort {
        private final Map<String, PokemonDetail> details = new HashMap<>();

        public Catalog with(PokemonDetail detail) {
            details.put(detail.name(), detail);
            details.put(Integer.toString(detail.nationalId()), detail);
            return this;
        }

        @Override
        public PageResult<PokemonSummary> list(PageQuery query) {
            List<PokemonSummary> summaries = details.values().stream()
                    .filter(detail -> detail.name().equals(details.get(detail.name()).name()))
                    .map(PokemonDetail::toSummary)
                    .distinct()
                    .toList();
            return new PageResult<>(summaries, query.page(), query.size(), summaries.size());
        }

        @Override
        public PokemonDetail getByNameOrId(String nameOrId) {
            PokemonDetail detail = details.get(nameOrId);
            if (detail == null) {
                throw new PokemonNotFoundException(nameOrId);
            }
            return detail;
        }
    }

    public static final class LocalStore implements LocalPokemonRepository {
        private final Map<UUID, LocalPokemon> rows = new HashMap<>();

        @Override
        public Optional<LocalPokemon> findById(UUID id) {
            return Optional.ofNullable(rows.get(id));
        }

        @Override
        public Optional<LocalPokemon> findByNationalId(int nationalId) {
            return rows.values().stream().filter(pokemon -> pokemon.nationalId() == nationalId).findFirst();
        }

        @Override
        public PageResult<LocalPokemon> findAll(PageQuery query) {
            List<LocalPokemon> all = new ArrayList<>(rows.values());
            int from = Math.min(query.offset(), all.size());
            int to = Math.min(from + query.size(), all.size());
            return new PageResult<>(List.copyOf(all.subList(from, to)), query.page(), query.size(), all.size());
        }

        @Override
        public LocalPokemon save(LocalPokemon pokemon) {
            rows.put(pokemon.id(), pokemon);
            return pokemon;
        }

        @Override
        public void deleteById(UUID id) {
            rows.remove(id);
        }
    }

    public static final class Users implements UserRepository {
        private final Map<UUID, UserAccount> rows = new HashMap<>();

        @Override
        public Optional<UserAccount> findById(UUID id) {
            return Optional.ofNullable(rows.get(id));
        }

        @Override
        public Optional<UserAccount> findByEmail(String email) {
            return rows.values().stream().filter(user -> user.email().equals(email)).findFirst();
        }

        @Override
        public List<UserAccount> findAll() {
            return List.copyOf(rows.values());
        }

        @Override
        public UserAccount save(UserAccount user) {
            rows.put(user.id(), user);
            return user;
        }

        @Override
        public void deleteById(UUID id) {
            rows.remove(id);
        }

        @Override
        public boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }
    }

    public static final class Hasher implements PasswordHasher {
        @Override
        public String hash(String rawPassword) {
            return "hash:" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String passwordHash) {
            return hash(rawPassword).equals(passwordHash);
        }
    }

    public static final class Tokens implements TokenIssuer {
        @Override
        public AuthToken issue(UserAccount user) {
            return new AuthToken("token-" + user.email(), 60);
        }

        @Override
        public Optional<AuthenticatedUser> parse(String token) {
            return Optional.empty();
        }
    }

    public static UserAccount user(Role role) {
        return UserAccount.create("trainer@pokemon.local", "Trainer", "hash:secret123", role);
    }
}
