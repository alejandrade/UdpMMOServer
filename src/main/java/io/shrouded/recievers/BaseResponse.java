package io.shrouded.recievers;

import io.shrouded.recievers.ping.PongMessageResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
@Getter
public class BaseResponse<T extends PayloadMessageResponse> {
    private final ResponseMessageType type;
    private final T payload;

    public byte[] toBytes() {
        byte[] payloadBytes;

        switch (type) {
            case pong -> {
                payloadBytes = ((PongMessageResponse) payload).toBytes();
            }
            case error -> {
                payloadBytes = ((ErrorMessageResponse) payload).toBytes();
            }
            case success, debug -> {
                payloadBytes = new byte[0];
            }
            default -> throw new IllegalArgumentException("Unknown response type: " + type);
        }

        ByteBuffer buffer = ByteBuffer.allocate(1 + payloadBytes.length);
        buffer.put(type.getCode());
        buffer.put(payloadBytes);
        return buffer.array();
    }

    public static BaseResponse<? extends PayloadMessageResponse> fromBytes(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte typeCode = buffer.get();
        ResponseMessageType type = ResponseMessageType.fromCode(typeCode);

        PayloadMessageResponse payload = null;

        switch (type) {
            case pong -> {
                byte[] payloadBytes = new byte[buffer.remaining()];
                buffer.get(payloadBytes);
                payload = PongMessageResponse.fromBytes(payloadBytes);
            }
            case success, error, debug -> {
                // payload stays null for these
            }
            default -> throw new IllegalArgumentException("Unknown response type: " + type);
        }

        return new BaseResponse<>(type, payload);
    }
}