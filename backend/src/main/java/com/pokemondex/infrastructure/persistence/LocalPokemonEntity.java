package com.pokemondex.infrastructure.persistence;

import com.pokemondex.domain.model.EvolutionStage;
import com.pokemondex.domain.model.LocalPokemon;
import com.pokemondex.domain.model.PokemonStat;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "local_pokemon")
public class LocalPokemonEntity {

    @Id
    private UUID id;

    @Column(name = "national_id", nullable = false, unique = true)
    private int nationalId;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(name = "sprite_url", length = 500)
    private String spriteUrl;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "weight_hectograms", nullable = false)
    private int weightHectograms;

    @Column(length = 4000)
    private String description;

    @Column(name = "localized_name", nullable = false, length = 80)
    private String localizedName;

    @Column(nullable = false, length = 60)
    private String region;

    @Column(length = 2000)
    private String notes;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "pokemon_type", joinColumns = @JoinColumn(name = "pokemon_id"))
    @OrderColumn(name = "position")
    @Column(name = "type_name", nullable = false, length = 40)
    private List<String> types = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "pokemon_skill", joinColumns = @JoinColumn(name = "pokemon_id"))
    @OrderColumn(name = "position")
    @Column(name = "skill_name", nullable = false, length = 40)
    private List<String> skills = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "pokemon_tag", joinColumns = @JoinColumn(name = "pokemon_id"))
    @OrderColumn(name = "position")
    @Column(name = "tag_name", nullable = false, length = 30)
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "pokemon_stat", joinColumns = @JoinColumn(name = "pokemon_id"))
    @OrderColumn(name = "position")
    private List<StatEmbeddable> stats = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "pokemon_evolution", joinColumns = @JoinColumn(name = "pokemon_id"))
    @OrderColumn(name = "stage_order")
    private List<EvolutionEmbeddable> evolutions = new ArrayList<>();

    protected LocalPokemonEntity() {
    }

    public void apply(LocalPokemon pokemon) {
        this.id = pokemon.id();
        this.nationalId = pokemon.nationalId();
        this.name = pokemon.name();
        this.spriteUrl = pokemon.spriteUrl();
        this.imageUrl = pokemon.imageUrl();
        this.weightHectograms = pokemon.weightHectograms();
        this.description = pokemon.description();
        this.localizedName = pokemon.localizedName();
        this.region = pokemon.region();
        this.notes = pokemon.notes();
        this.updatedAt = pokemon.updatedAt();
        replace(this.types, pokemon.types());
        replace(this.skills, pokemon.skills());
        replace(this.tags, pokemon.tags());
        this.stats.clear();
        pokemon.stats().forEach(stat -> this.stats.add(new StatEmbeddable(stat.name(), stat.value())));
        this.evolutions.clear();
        pokemon.evolutions().forEach(stage -> this.evolutions.add(
                new EvolutionEmbeddable(stage.nationalId(), stage.name(), stage.spriteUrl())
        ));
    }

    public LocalPokemon toDomain() {
        return new LocalPokemon(
                id,
                nationalId,
                name,
                spriteUrl,
                imageUrl,
                List.copyOf(types),
                weightHectograms,
                List.copyOf(skills),
                stats.stream().map(stat -> new PokemonStat(stat.getName(), stat.getValue())).toList(),
                description,
                evolutions.stream()
                        .map(stage -> new EvolutionStage(stage.getNationalId(), stage.getName(), stage.getSpriteUrl()))
                        .toList(),
                localizedName,
                region,
                List.copyOf(tags),
                notes == null ? "" : notes,
                updatedAt
        );
    }

    private static void replace(List<String> target, List<String> values) {
        target.clear();
        target.addAll(values);
    }
}
