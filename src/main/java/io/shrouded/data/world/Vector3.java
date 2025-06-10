package io.shrouded.data.world;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record Vector3(
        @NotNull Float x,
        @NotNull Float y,
        @NotNull Float z
) implements Serializable { }
