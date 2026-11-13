package com.test.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SongType {
    BGM("bgm"),
    COVER("cover");

    private final String value;

    SongType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SongType fromValue(String v) {
        if (v == null) return null;
        String normalized = v.trim().toLowerCase();
        for (SongType t : values()) {
            if (t.value.equals(normalized) || t.name().equalsIgnoreCase(normalized)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown SongType: " + v);
    }
}

