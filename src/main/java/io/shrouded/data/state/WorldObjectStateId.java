package io.shrouded.data.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * Represents any object in the world that can move,
 * such as a player, NPC, vehicle, or dynamic game entity.
 */
public record WorldObjectStateId(String value) implements Serializable {

    public WorldObjectStateId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("VolatileObjectId cannot be null or blank");
        }
    }

    @JsonCreator
    public static WorldObjectStateId of(String value) {
        return new WorldObjectStateId(value);
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
