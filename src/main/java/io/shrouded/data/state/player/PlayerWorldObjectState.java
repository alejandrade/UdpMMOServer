package io.shrouded.data.state.player;

import io.shrouded.data.state.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PlayerWorldObjectState(
        WorldObjectStateId worldObjectId,
        Vector3 position,
        Quaternion rotation,
        Vector3 velocity,
        int health,
        int energy,
        boolean isDead,
        boolean isInCombat,
        Integer castSpellId,
        UUID targetId,
        List<Integer> activeBuffs,
        List<Integer> activeDebuffs,
        Instant lastSavedUpdate
) implements WorldObject, Serializable, HasWorldCoordinates {

    // ✅ Minimal constructor overload
    public PlayerWorldObjectState(
            WorldObjectStateId worldObjectId,
            Vector3 position,
            Quaternion rotation,
            Vector3 velocity,
            int health,
            int mana,
            Instant lastSavedUpdate
    ) {
        this(
                worldObjectId,
                position,
                rotation,
                velocity,
                health,
                mana,
                false,         // isDead default
                false,         // isInCombat default
                null,          // castSpellId default
                null,          // targetId default
                List.of(),     // activeBuffs default
                List.of(),     // activeDebuffs default
                lastSavedUpdate
        );
    }

    public static PlayerWorldObjectState cloneWithDifferentUpdate(PlayerWorldObjectState playerWorldObjectState, Instant different) {
        return new PlayerWorldObjectState(
                playerWorldObjectState.worldObjectId,
                playerWorldObjectState.position,
                playerWorldObjectState.rotation,
                playerWorldObjectState.velocity,
                playerWorldObjectState.health,
                playerWorldObjectState.energy,
                playerWorldObjectState.isDead,
                playerWorldObjectState.isInCombat,
                playerWorldObjectState.castSpellId,
                playerWorldObjectState.targetId,
                playerWorldObjectState.activeBuffs,
                playerWorldObjectState.activeDebuffs,
                different
        );
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
