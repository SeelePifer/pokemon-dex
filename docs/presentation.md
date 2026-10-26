# Presentation script

Aim for about eight minutes, then leave the rest for the code review.

## 1. User stories (1 minute)

The service is a Pokedex with a local lab.

- Browse is a public, paginated view of sprite, types, kilograms, and abilities.
- Detail adds artwork, base stats, the English flavor text, and the evolution line.
- Sync copies one Pokemon into PostgreSQL so the team can attach fields PokeAPI does not have: a localized name, a region, tags, and notes.
- Update changes only those proprietary fields. A missing id is 404. A blank name, a bad tag, or a broken UUID is 400.

Show the mapping table in the README while you say this.

## 2. Architecture (2 minutes)

Draw the four layers: web, application, domain, infrastructure.

- Use cases are plain Java. Spring only wires them in `UseCaseConfig`.
- Ports live in the domain. The PokeAPI client and the JPA adapters implement them.
- `ArchitectureTest` locks that dependency direction.
- Caching sits in the adapter, not in the controller. A page fans out to detail calls because the list endpoint has no sprite or weight. Each detail is cached, and so is the finished page, for 30 minutes.
- Sync is idempotent. The national id is unique. A second sync refreshes the snapshot and keeps the lab fields.

## 3. Demo (4 minutes)

1. Open http://localhost:5173 and page through the live catalog.
2. Open Bulbasaur. Point at the art, the stat bars, the description, and the evolution links.
3. Open Local lab and show the three seeded starters with Kanto and the internal tags. This screen is public.
4. Try Save on the detail page before login. The UI sends you to sign in.
5. Sign in as `demo@pokemon.local` / `Demo1234!`.
6. Save Pikachu, then edit region to `Lab` and add a tag. Refresh and show the values stuck.
7. Mention Swagger at `/swagger-ui.html`, the 401 on sync without a token, and that admin routes are a separate role. The admin account is `admin@pokemon.local` / `Admin1234!`.

## 4. Tests and review hooks (1 minute)

`mvn test` is the gate. Call out one use-case test that proves sync keeps notes, one MockMvc test for 400 and 404, and the security flow that uses the seeded password.

If asked about Generative AI, open `docs/genai-task-api.md` and walk the prompt, the sample, and the corrections: do not trust a client-supplied user id, distinguish 400 from 404, and keep authorization in the use case.
