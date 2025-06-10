package io.shrouded.data.world;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * Represents any object in the world that can move,
 * such as a player, NPC, vehicle, or dynamic game entity.
 */
public record WorldObjectId(String value) implements Serializable {

    public WorldObjectId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("VolatileObjectId cannot be null or blank");
        }
    }

    @JsonCreator
    public static WorldObjectId of(String value) {
        return new WorldObjectId(value);
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
