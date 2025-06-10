package io.shrouded.data.world;

import java.io.Serializable;

public interface WorldObject extends Serializable {
    WorldObjectId getId(); // or any shared contract
    @SuppressWarnings("unchecked")
    default <T extends WorldObject> T ofType(Class<T> clazz) {
        if (!clazz.isInstance(this)) {
            throw new ClassCastException("Object is not of type " + clazz.getName());
        }
        return (T) this;
    }
}
