package io.shrouded.recievers;

import io.shrouded.recievers.connect.ConnectReceiver;
import io.shrouded.recievers.state.StateReceiver;
import io.shrouded.recievers.ping.PingReceiver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RequestMessageType {
    connect((byte) 1, ConnectReceiver.class),
    ping((byte) 2, PingReceiver.class),
    state((byte) 3, StateReceiver.class);

    private final byte code;
    private final Class<? extends MessageReceiver> aClass;

    public static RequestMessageType fromCode(byte code) {
        for (RequestMessageType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown RequestMessageType code: " + code);
    }
}
