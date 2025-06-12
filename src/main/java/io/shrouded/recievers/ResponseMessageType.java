package io.shrouded.recievers;

import lombok.Getter;

@Getter
public enum ResponseMessageType {
    pong((byte) 1),
    success((byte) 2),
    error((byte) 3),
    debug((byte) 4);

    private final byte code;

    ResponseMessageType(byte code) {
        this.code = code;
    }

    public static ResponseMessageType fromCode(byte code) {
        for (ResponseMessageType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ResponseMessageType code: " + code);
    }
}
