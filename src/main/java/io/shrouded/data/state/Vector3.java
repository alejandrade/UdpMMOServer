package io.shrouded.data.state;

import java.io.Serializable;

public record Vector3(
        float x,
        float y,
        float z
) implements Serializable { }
