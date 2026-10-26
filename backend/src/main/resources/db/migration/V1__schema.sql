CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(80) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE local_pokemon (
    id UUID PRIMARY KEY,
    national_id INTEGER NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL,
    sprite_url VARCHAR(500),
    image_url VARCHAR(500),
    weight_hectograms INTEGER NOT NULL,
    description VARCHAR(4000),
    localized_name VARCHAR(80) NOT NULL,
    region VARCHAR(60) NOT NULL,
    notes VARCHAR(2000),
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE pokemon_type (
    pokemon_id UUID NOT NULL,
    position INTEGER NOT NULL,
    type_name VARCHAR(40) NOT NULL,
    PRIMARY KEY (pokemon_id, position)
);

CREATE TABLE pokemon_skill (
    pokemon_id UUID NOT NULL,
    position INTEGER NOT NULL,
    skill_name VARCHAR(40) NOT NULL,
    PRIMARY KEY (pokemon_id, position)
);

CREATE TABLE pokemon_tag (
    pokemon_id UUID NOT NULL,
    position INTEGER NOT NULL,
    tag_name VARCHAR(30) NOT NULL,
    PRIMARY KEY (pokemon_id, position)
);

CREATE TABLE pokemon_stat (
    pokemon_id UUID NOT NULL,
    position INTEGER NOT NULL,
    stat_name VARCHAR(40) NOT NULL,
    stat_value INTEGER NOT NULL,
    PRIMARY KEY (pokemon_id, position)
);

CREATE TABLE pokemon_evolution (
    pokemon_id UUID NOT NULL,
    stage_order INTEGER NOT NULL,
    national_id INTEGER NOT NULL,
    species_name VARCHAR(80) NOT NULL,
    sprite_url VARCHAR(500),
    PRIMARY KEY (pokemon_id, stage_order)
);
