# Pokemon Dex

Interview project for a Spring Boot API that browses [PokeAPI](https://pokeapi.co/docs/v2), stores a local copy with proprietary fields, and serves a React client.

## User stories

| Story | What it does | Endpoint |
| --- | --- | --- |
| US01 Browse | Paginated sprite, types, mass in kilograms, and abilities | `GET /api/v1/pokemon?page=0&size=12` |
| US02 Detail | Artwork, base stats, English description, evolution line | `GET /api/v1/pokemon/{nameOrId}` |
| US03 Sync | Copies a Pokemon into PostgreSQL and keeps room for local fields | `POST /api/v1/local-pokemon/sync/{nameOrId}` |
| US04 Update | Edits localized name, region, tags, and notes | `PUT /api/v1/local-pokemon/{id}` |

Public reads do not need a token. Sync, update, and delete need a Bearer token. `GET /api/v1/users` and `DELETE /api/v1/users/{id}` need an admin token.

Repeating a sync for the same national id refreshes the PokeAPI snapshot and leaves proprietary fields in place.

## Architecture

```text
web controllers -> application use cases -> domain ports
                                      \-> PokeAPI adapter + Caffeine
                                      \-> JPA adapter -> PostgreSQL
```

`domain` and `application` do not import Spring, JPA, or HTTP. `ArchitectureTest` fails the build if that boundary moves. Controllers call use cases only.

PokeAPI list results are names only, so a page loads each detail with a small thread pool (8 in production, 1 in tests). Detail responses and assembled pages are cached in Caffeine for 30 minutes (`pokemonDetails`, `pokemonPages`).

Weight is stored in hectograms, which is the PokeAPI unit, and returned as `massKg`.

## Prerequisites

- JDK 21
- Maven 3.9+
- Node.js 20
- Docker, if you want the full stack in containers
- PostgreSQL 16, if you run the API on the host

## Demo accounts

Seeded on startup. Passwords are hashed with BCrypt and are not stored in SQL.

- Trainer: `demo@pokemon.local` / `Demo1234!`
- Admin: `admin@pokemon.local` / `Admin1234!`

Flyway also seeds Bulbasaur, Charmander, and Squirtle with region `Kanto` and internal tags so the local lab works before a live sync.

## Run with Docker

From the repository root:

```powershell
docker compose up --build
```

- App: http://localhost:5173
- API and Swagger: http://localhost:8080/swagger-ui.html
- Postgres: `localhost:5433`, database `pokemondex`, user `pokemon`, password `pokemon`

The frontend nginx container proxies `/api` to the API, so the browser uses one origin.

## Run on the host

Start PostgreSQL with database `pokemondex`, user `pokemon`, and password `pokemon`, listening on `localhost:5433`. Compose publishes that port so it does not collide with another Postgres already using 5432. Then:

```powershell
cd backend
mvn spring-boot:run
```

```powershell
cd frontend
npm install
npm run dev
```

The Vite dev server proxies `/api` to `http://localhost:8080`.

Override the database or JWT secret with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, and `JWT_SECRET`. The JWT secret must be at least 32 bytes.

## Tests

```powershell
cd backend
mvn test
```

Tests use H2 in PostgreSQL mode and do not need Docker. Coverage includes domain rules, use cases with in-memory ports, PokeAPI JSON mapping and response caching, MockMvc status codes (200, 400, 401, 403, 404, 409, 502), a JPA adapter test, an ArchUnit boundary test, and a Spring Boot flow that logs in as the demo user, syncs, and updates.

## API errors

Every error uses the same JSON shape: `timestamp`, `status`, `error`, `message`, `path`, and `fieldErrors`.

- `400` malformed JSON, failed bean validation, bad page size, or an invalid Pokemon name
- `401` missing token or bad credentials
- `403` a trainer calling an admin route
- `404` unknown PokeAPI name, or a local id that is not stored
- `409` duplicate registration
- `502` PokeAPI timeout or unexpected status

## Publish a public Git repository

This folder is a local git repository. To publish it:

```powershell
git add .
git commit -m "Add Pokemon Dex interview project"
gh repo create pokemon-dex --public --source=. --remote=origin --push
```

Use GitHub, GitLab, or another host if you prefer. Do not commit secrets beyond the documented demo JWT default.

## More reading

- [Presentation script](docs/presentation.md)
- [Generative AI exercise](docs/genai-task-api.md)
