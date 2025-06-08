package io.shrouded.recievers;

import io.shrouded.udpConnectionHelper;
import io.shrouded.recievers.request.PayloadMessageRequest;
import io.shrouded.recievers.response.PayloadMessageResponse;

public interface MessageReceiver<T extends PayloadMessageRequest, R extends PayloadMessageResponse> {
    void handle(String requestId, T payloadMessage, udpConnectionHelper publisherHelper);
}
