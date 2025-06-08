package io.shrouded.recievers.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PongMessageResponse.class, name = "pong")
            // Add other subtypes here as needed
    })
    private final T payload;
}
