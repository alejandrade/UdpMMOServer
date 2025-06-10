package io.shrouded.data.world;

import java.io.Serializable;

public record PlayerWorldObject(
        WorldObjectId worldObjectId,
        Vector3 position,
        Quaternion rotation,
        Vector3 velocity
) implements WorldObject, Serializable, HasWorldCoordinates {
    @Override
    public WorldObjectId getId() {
        return worldObjectId;
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
