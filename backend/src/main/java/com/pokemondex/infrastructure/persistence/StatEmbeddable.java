package com.pokemondex.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class StatEmbeddable {

    @Column(name = "stat_name", nullable = false, length = 40)
    private String name;

    @Column(name = "stat_value", nullable = false)
    private int value;

    protected StatEmbeddable() {
    }

    public StatEmbeddable(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
