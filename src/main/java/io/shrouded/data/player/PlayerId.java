package io.shrouded.data.player;

public record PlayerId(String value) {

    public PlayerId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PlayerId cannot be null or blank");
        }
    }

    public static PlayerId of(String value) {
        return new PlayerId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}