package io.shrouded.recievers.move;
import io.shrouded.data.state.Quaternion;
import io.shrouded.data.state.Vector3;

import io.shrouded.recievers.PayloadMessageRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record MoveRequest(
        @NotNull @Valid Vector3 position,
        @NotNull @Valid Quaternion rotation,
        @NotNull @Valid Vector3 velocity
) implements Serializable, PayloadMessageRequest { }
