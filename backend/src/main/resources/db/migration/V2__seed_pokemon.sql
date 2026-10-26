INSERT INTO local_pokemon (
    id, national_id, name, sprite_url, image_url, weight_hectograms, description,
    localized_name, region, notes, updated_at
) VALUES
(
    '11111111-1111-1111-1111-111111111111', 1, 'bulbasaur',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png',
    69, 'A strange seed was planted on its back at birth. The plant sprouts and grows with this Pokemon.',
    'Bulbasaur', 'Kanto', 'Seeded for the interview demo.', TIMESTAMP '2026-01-01 00:00:00'
),
(
    '22222222-2222-2222-2222-222222222222', 4, 'charmander',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png',
    85, 'The flame on its tail shows the strength of its life force.',
    'Charmander', 'Kanto', 'Seeded fire starter.', TIMESTAMP '2026-01-01 00:00:00'
),
(
    '33333333-3333-3333-3333-333333333333', 7, 'squirtle',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png',
    90, 'It shelters itself in its shell and strikes back with water.',
    'Squirtle', 'Kanto', 'Seeded water starter.', TIMESTAMP '2026-01-01 00:00:00'
);

INSERT INTO pokemon_type (pokemon_id, position, type_name) VALUES
('11111111-1111-1111-1111-111111111111', 0, 'grass'),
('11111111-1111-1111-1111-111111111111', 1, 'poison'),
('22222222-2222-2222-2222-222222222222', 0, 'fire'),
('33333333-3333-3333-3333-333333333333', 0, 'water');

INSERT INTO pokemon_skill (pokemon_id, position, skill_name) VALUES
('11111111-1111-1111-1111-111111111111', 0, 'overgrow'),
('11111111-1111-1111-1111-111111111111', 1, 'chlorophyll'),
('22222222-2222-2222-2222-222222222222', 0, 'blaze'),
('22222222-2222-2222-2222-222222222222', 1, 'solar-power'),
('33333333-3333-3333-3333-333333333333', 0, 'torrent'),
('33333333-3333-3333-3333-333333333333', 1, 'rain-dish');

INSERT INTO pokemon_tag (pokemon_id, position, tag_name) VALUES
('11111111-1111-1111-1111-111111111111', 0, 'starter'),
('11111111-1111-1111-1111-111111111111', 1, 'internal-grass'),
('22222222-2222-2222-2222-222222222222', 0, 'starter'),
('22222222-2222-2222-2222-222222222222', 1, 'internal-fire'),
('33333333-3333-3333-3333-333333333333', 0, 'starter'),
('33333333-3333-3333-3333-333333333333', 1, 'internal-water');

INSERT INTO pokemon_stat (pokemon_id, position, stat_name, stat_value) VALUES
('11111111-1111-1111-1111-111111111111', 0, 'hp', 45),
('11111111-1111-1111-1111-111111111111', 1, 'attack', 49),
('11111111-1111-1111-1111-111111111111', 2, 'defense', 49),
('11111111-1111-1111-1111-111111111111', 3, 'special-attack', 65),
('11111111-1111-1111-1111-111111111111', 4, 'special-defense', 65),
('11111111-1111-1111-1111-111111111111', 5, 'speed', 45),
('22222222-2222-2222-2222-222222222222', 0, 'hp', 39),
('22222222-2222-2222-2222-222222222222', 1, 'attack', 52),
('22222222-2222-2222-2222-222222222222', 2, 'defense', 43),
('22222222-2222-2222-2222-222222222222', 3, 'special-attack', 60),
('22222222-2222-2222-2222-222222222222', 4, 'special-defense', 50),
('22222222-2222-2222-2222-222222222222', 5, 'speed', 65),
('33333333-3333-3333-3333-333333333333', 0, 'hp', 44),
('33333333-3333-3333-3333-333333333333', 1, 'attack', 48),
('33333333-3333-3333-3333-333333333333', 2, 'defense', 65),
('33333333-3333-3333-3333-333333333333', 3, 'special-attack', 50),
('33333333-3333-3333-3333-333333333333', 4, 'special-defense', 64),
('33333333-3333-3333-3333-333333333333', 5, 'speed', 43);

INSERT INTO pokemon_evolution (pokemon_id, stage_order, national_id, species_name, sprite_url) VALUES
('11111111-1111-1111-1111-111111111111', 0, 1, 'bulbasaur', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png'),
('11111111-1111-1111-1111-111111111111', 1, 2, 'ivysaur', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png'),
('11111111-1111-1111-1111-111111111111', 2, 3, 'venusaur', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/3.png'),
('22222222-2222-2222-2222-222222222222', 0, 4, 'charmander', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png'),
('22222222-2222-2222-2222-222222222222', 1, 5, 'charmeleon', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/5.png'),
('22222222-2222-2222-2222-222222222222', 2, 6, 'charizard', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png'),
('33333333-3333-3333-3333-333333333333', 0, 7, 'squirtle', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png'),
('33333333-3333-3333-3333-333333333333', 1, 8, 'wartortle', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/8.png'),
('33333333-3333-3333-3333-333333333333', 2, 9, 'blastoise', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/9.png');
