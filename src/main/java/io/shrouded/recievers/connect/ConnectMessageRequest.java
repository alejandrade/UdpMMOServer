package io.shrouded.recievers.connect;

import io.shrouded.recievers.PayloadMessageRequest;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public record ConnectMessageRequest(String jwt)
        implements PayloadMessageRequest {

    @Override
    public byte[] getBytes() {
        byte[] jwtBytes = jwt.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(2 + jwtBytes.length);
        buffer.putShort((short) jwtBytes.length);
        buffer.put(jwtBytes);
        return buffer.array();
    }

    public static ConnectMessageRequest fromBytes(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        short length = buffer.getShort();
        byte[] jwtBytes = new byte[length];
        buffer.get(jwtBytes);
        String jwt = new String(jwtBytes, StandardCharsets.UTF_8);
        return new ConnectMessageRequest(jwt);
    }
}
