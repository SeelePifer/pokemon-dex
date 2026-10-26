package com.pokemondex.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EvolutionEmbeddable {

    @Column(name = "national_id", nullable = false)
    private int nationalId;

    @Column(name = "species_name", nullable = false, length = 80)
    private String name;

    @Column(name = "sprite_url", length = 500)
    private String spriteUrl;

    protected EvolutionEmbeddable() {
    }

    public EvolutionEmbeddable(int nationalId, String name, String spriteUrl) {
        this.nationalId = nationalId;
        this.name = name;
        this.spriteUrl = spriteUrl;
    }

    public int getNationalId() {
        return nationalId;
    }

    public String getName() {
        return name;
    }

    public String getSpriteUrl() {
        return spriteUrl;
    }
}
