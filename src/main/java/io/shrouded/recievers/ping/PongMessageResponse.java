package io.shrouded.recievers.ping;

import io.shrouded.recievers.PayloadMessageResponse;

public record PongMessageResponse(boolean registered) implements PayloadMessageResponse {

    @Override
    public byte[] toBytes() {
        return new byte[]{(byte) (registered ? 1 : 0)};
    }

    public static PongMessageResponse fromBytes(byte[] data) {
        if (data.length != 1) {
            throw new IllegalArgumentException("Invalid PongMessageResponse length");
        }
        return new PongMessageResponse(data[0] == 1);
    }
}
