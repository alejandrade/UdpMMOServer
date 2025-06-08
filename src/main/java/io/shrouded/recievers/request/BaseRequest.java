package io.shrouded.recievers.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.shrouded.recievers.RequestMessageType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BaseRequest {
    private String requestId;
    private RequestMessageType type;
    private Instant requestSent;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PingMessageRequest.class, name = "ping"),
            @JsonSubTypes.Type(value = ConnectMessageRequest.class, name = "connect")
    })
    private PayloadMessageRequest payload;
}
