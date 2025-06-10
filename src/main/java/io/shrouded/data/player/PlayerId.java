package io.shrouded.data.player;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record PlayerId(String value) {

    public PlayerId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PlayerId cannot be null or blank");
        }
    }

    @JsonCreator
    public static PlayerId of(String value) {
        return new PlayerId(value);
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}