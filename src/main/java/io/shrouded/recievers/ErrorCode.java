package io.shrouded.recievers;

import lombok.Getter;

@Getter
public enum ErrorCode {
    unknown((byte) 1),
    invalidRequest((byte) 2),
    authFailed((byte) 3),
    notLoggedIn((byte) 4),
    serverError((byte) 5);

    private final byte code;

    ErrorCode(byte code) {
        this.code = code;
    }

    public static ErrorCode fromCode(byte code) {
        for (ErrorCode ec : values()) {
            if (ec.code == code) {
                return ec;
            }
        }
        return unknown; // fallback for unknown error codes
    }
}
