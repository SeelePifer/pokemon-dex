package com.pokemondex.domain.model;

import com.pokemondex.domain.exception.InvalidRequestException;

import java.util.LinkedHashSet;
import java.util.List;

public final class ProprietaryFields {

    public static final int NAME_MAX = 80;
    public static final int REGION_MAX = 60;
    public static final int NOTES_MAX = 500;
    public static final int TAG_MAX = 10;

    private ProprietaryFields() {
    }

    public static String requireText(String value, String field, int max) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(field + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() > max) {
            throw new InvalidRequestException(field + " must be at most " + max + " characters");
        }
        return trimmed;
    }

    public static String notes(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.length() > NOTES_MAX) {
            throw new InvalidRequestException("Notes must be at most " + NOTES_MAX + " characters");
        }
        return trimmed;
    }

    public static List<String> normalizeTags(List<String> tags) {
        if (tags == null) {
            throw new InvalidRequestException("Tags are required");
        }
        if (tags.size() > TAG_MAX) {
            throw new InvalidRequestException("At most " + TAG_MAX + " tags are allowed");
        }
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String tag : tags) {
            if (tag == null || !tag.matches("[a-z0-9-]{1,30}")) {
                throw new InvalidRequestException(
                        "Tags must be 1 to 30 characters and use only lowercase letters, numbers, and hyphens"
                );
            }
            unique.add(tag);
        }
        return List.copyOf(unique);
    }
}
