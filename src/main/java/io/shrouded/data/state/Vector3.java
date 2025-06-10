package io.shrouded.data.state;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record Vector3(
        @NotNull Float x,
        @NotNull Float y,
        @NotNull Float z
) implements Serializable { }
