package io.shrouded.recievers;

import io.shrouded.UdpConnectionHelper;

public interface MessageReceiver<T extends PayloadMessageRequest, R extends PayloadMessageResponse> {
    void handle(String requestId, T payloadMessage, UdpConnectionHelper publisherHelper);
    boolean isPrivate();
}
