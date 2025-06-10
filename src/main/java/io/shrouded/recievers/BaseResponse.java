package io.shrouded.recievers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.shrouded.recievers.ping.PongMessageResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
@Getter
public class BaseResponse<T extends PayloadMessageResponse> {
    private final String requestId;
    private final Instant responseSent;
    private final ResponseMessageType type;
    private final int status;
    private final String message;
    private final T payload;
}
