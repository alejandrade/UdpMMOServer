package io.shrouded.recievers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.shrouded.recievers.connect.ConnectMessageRequest;
import io.shrouded.recievers.move.MoveRequest;
import io.shrouded.recievers.ping.PingMessageRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BaseRequest {

    @NotBlank // ensures not null and not empty string
    private String requestId;

    @NotNull // must be present
    private RequestMessageType type;

    @NotNull // ISO instant must be present
    private Instant requestSent;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PingMessageRequest.class, name = "ping"),
            @JsonSubTypes.Type(value = ConnectMessageRequest.class, name = "connect"),
            @JsonSubTypes.Type(value = MoveRequest.class, name = "move")
    })
    @Valid // triggers validation on nested payload object
    @NotNull // payload must not be null
    private PayloadMessageRequest payload;
}
