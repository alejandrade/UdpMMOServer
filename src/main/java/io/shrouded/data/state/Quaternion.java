package io.shrouded.data.state;
import java.io.Serializable;

public record Quaternion(
        float x,
        float y,
        float z,
        float w
) implements Serializable { }