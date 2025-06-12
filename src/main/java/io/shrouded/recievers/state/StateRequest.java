package io.shrouded.recievers.state;

import io.shrouded.data.state.Quaternion;
import io.shrouded.data.state.Vector3;
import io.shrouded.recievers.PayloadMessageRequest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record StateRequest(
        Vector3 position,
        Quaternion rotation,
        Vector3 velocity,
        int health,
        int energy
) implements PayloadMessageRequest {

    public static StateRequest fromBytes(byte[] data) {
        if (data.length != 48) {
            throw new IllegalArgumentException("Invalid StateRequest packet length: " + data.length);
        }

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        // Read position
        float posX = buffer.getFloat();
        float posY = buffer.getFloat();
        float posZ = buffer.getFloat();
        Vector3 position = new Vector3(posX, posY, posZ);

        // Read rotation
        float rotX = buffer.getFloat();
        float rotY = buffer.getFloat();
        float rotZ = buffer.getFloat();
        float rotW = buffer.getFloat();
        Quaternion rotation = new Quaternion(rotX, rotY, rotZ, rotW);

        // Read velocity
        float velX = buffer.getFloat();
        float velY = buffer.getFloat();
        float velZ = buffer.getFloat();
        Vector3 velocity = new Vector3(velX, velY, velZ);

        // Read health and energy
        int health = buffer.getInt();
        int energy = buffer.getInt();

        return new StateRequest(position, rotation, velocity, health, energy);
    }

    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN);

        // Write position
        buffer.putFloat(position.x());
        buffer.putFloat(position.y());
        buffer.putFloat(position.z());

        // Write rotation
        buffer.putFloat(rotation.x());
        buffer.putFloat(rotation.y());
        buffer.putFloat(rotation.z());
        buffer.putFloat(rotation.w());

        // Write velocity
        buffer.putFloat(velocity.x());
        buffer.putFloat(velocity.y());
        buffer.putFloat(velocity.z());

        // Write health and energy
        buffer.putInt(health);
        buffer.putInt(energy);

        return buffer.array();
    }
}
