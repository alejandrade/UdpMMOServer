package io.shrouded.data.state.player;

import io.shrouded.data.state.*;

import java.io.Serializable;
import java.time.Instant;

public record PlayerWorldObjectState(
        WorldObjectStateId worldObjectId,
        Vector3 position,
        Quaternion rotation,
        Vector3 velocity,
        Instant lastSavedUpdate
) implements WorldObject, Serializable, HasWorldCoordinates {

    public static PlayerWorldObjectState cloneWithDifferentUpdate(PlayerWorldObjectState playerWorldObjectState, Instant different) {
        return new PlayerWorldObjectState(playerWorldObjectState.worldObjectId,
                playerWorldObjectState.position, playerWorldObjectState.rotation, playerWorldObjectState.velocity, different);
    }

    @Override
    public WorldObjectStateId getId() {
        return worldObjectId;
    }

    @Override
    public Instant getLastUpdate() {
        return lastSavedUpdate;
    }

    @Override
    public double getX() {
        return position.x();
    }

    @Override
    public double getZ() {
        return position.z();
    }
}
