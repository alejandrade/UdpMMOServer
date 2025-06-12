package io.shrouded.recievers;

public record ErrorMessageResponse(ErrorCode errorCode) implements PayloadMessageResponse {

    @Override
    public byte[] toBytes() {
        return new byte[]{errorCode.getCode()};
    }

    public static ErrorMessageResponse fromBytes(byte[] data) {
        if (data.length != 1) {
            throw new IllegalArgumentException("Invalid ErrorMessageResponse length");
        }
        ErrorCode code = ErrorCode.fromCode(data[0]);
        return new ErrorMessageResponse(code);
    }
}
