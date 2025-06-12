package io.shrouded.data.entity.PlayerWorldObject;

import io.shrouded.data.entity.player.PlayerId;
import io.shrouded.data.state.player.PlayerWorldObjectState;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record PlayerWorldObjectEntity(
        PlayerId id,

        float positionX,
        float positionY,
        float positionZ,

        float rotationX,
        float rotationY,
        float rotationZ,
        float rotationW,

        float velocityX,
        float velocityY,
        float velocityZ,

        int health,
        int energy,

        boolean isDead,
        boolean isInCombat,

        Integer castSpellId,
        UUID targetId,

        List<Integer> activeBuffs,
        List<Integer> activeDebuffs
) implements Serializable {

    // ✅ This is your original game state converter (unchanged)
    public static PlayerWorldObjectEntity from(PlayerWorldObjectState source) {
        return new PlayerWorldObjectEntity(
                new PlayerId(source.getId().value()),
                source.position().x(),
                source.position().y(),
                source.position().z(),
                source.rotation().x(),
                source.rotation().y(),
                source.rotation().z(),
                source.rotation().w(),
                source.velocity().x(),
                source.velocity().y(),
                source.velocity().z(),
                source.health(),
                source.energy(),
                false,      // default isDead
                false,      // default isInCombat
                null,       // default castSpellId
                null,       // default targetId
                List.of(),  // default activeBuffs
                List.of()   // default activeDebuffs
        );
    }

}
