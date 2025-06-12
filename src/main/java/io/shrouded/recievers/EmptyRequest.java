package io.shrouded.recievers;

public class EmptyRequest implements PayloadMessageRequest{
    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException("this is an empty request");
    }
}
