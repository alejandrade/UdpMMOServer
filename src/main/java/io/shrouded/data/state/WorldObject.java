package io.shrouded.data.state;

import java.io.Serializable;
import java.time.Instant;

public interface WorldObject extends Serializable {
    WorldObjectStateId getId(); // or any shared contract

    Instant getLastUpdate();

    @SuppressWarnings("unchecked")
    default <T extends WorldObject> T ofType(Class<T> clazz) {
        if (!clazz.isInstance(this)) {
            throw new ClassCastException("Object is not of type " + clazz.getName());
        }
        return (T) this;
    }
}
