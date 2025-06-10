package io.shrouded.recievers.ping;

import io.shrouded.recievers.PayloadMessageResponse;

public record PongMessageResponse(boolean registered) implements PayloadMessageResponse {
}
