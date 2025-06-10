package io.shrouded.data.entity.PlayerWorldObject;

import io.shrouded.data.entity.player.PlayerId;
import io.shrouded.data.state.player.PlayerWorldObjectState;

import java.io.Serializable;

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
        float velocityZ
) implements Serializable {
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
                source.velocity().z()
        );
    }

}
