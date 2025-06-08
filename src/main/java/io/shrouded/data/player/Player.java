package io.shrouded.data.player;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("players")
public class Player {
    @Id
    private ObjectId id;
    private String name;

    public PlayerId getPlayerId() {
        return PlayerId.of(id.toHexString());
    }

    public void setPlayerId(PlayerId playerId) {
        this.id = new ObjectId(playerId.value());
    }
}
