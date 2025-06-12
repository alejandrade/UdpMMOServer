package io.shrouded.recievers;

public record EmptyResponse() implements PayloadMessageResponse {
    @Override
    public byte[] toBytes() {
        throw new UnsupportedOperationException("no");
    }
}
