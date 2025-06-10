package io.shrouded.data.state;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record Quaternion(
        @NotNull Float x,
        @NotNull Float y,
        @NotNull Float z,
        @NotNull Float w
) implements Serializable { }