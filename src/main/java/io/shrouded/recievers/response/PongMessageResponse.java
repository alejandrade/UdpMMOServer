package io.shrouded.recievers.response;

public record PongMessageResponse(boolean registered) implements PayloadMessageResponse {
}
