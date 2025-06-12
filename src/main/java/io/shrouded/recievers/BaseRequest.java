package io.shrouded.recievers;

import io.shrouded.recievers.connect.ConnectMessageRequest;
import io.shrouded.recievers.state.StateRequest;
import io.shrouded.util.RequestHasher;

import java.nio.ByteBuffer;

public class BaseRequest {
    private RequestMessageType type;
    private PayloadMessageRequest payload;

    public RequestMessageType getType() {
        return type;
    }

    public PayloadMessageRequest getPayload() {
        return payload;
    }

    public BaseRequest(RequestMessageType type, PayloadMessageRequest payload) {
        this.type = type;
        this.payload = payload;
    }

    public static BaseRequest fromBytes(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        byte typeCode = buffer.get();
        RequestMessageType type = RequestMessageType.fromCode(typeCode);

        PayloadMessageRequest payload;

        switch (type) {
            case connect -> {
                byte[] payloadBytes = new byte[buffer.remaining()];
                buffer.get(payloadBytes);
                payload = ConnectMessageRequest.fromBytes(payloadBytes);
            }
            case state -> {
                byte[] payloadBytes = new byte[buffer.remaining()];
                buffer.get(payloadBytes);
                payload = StateRequest.fromBytes(payloadBytes);
            }
            case ping -> {
                // no payload, instantiate directly
                payload = null;
            }
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }

        return new BaseRequest(type, payload);
    }

    public byte[] getBytes() {
        byte[] payloadBytes = switch (type) {
            case connect -> ((ConnectMessageRequest) payload).getBytes();
            case state -> ((StateRequest) payload).getBytes();
            case ping -> new byte[0]; // no payload for ping
        };

        ByteBuffer buffer = ByteBuffer.allocate(1 + payloadBytes.length);
        buffer.put(type.getCode());
        buffer.put(payloadBytes);

        return buffer.array();
    }

    public String getHash() {
        return RequestHasher.hashRequest(getBytes());
    }

}
