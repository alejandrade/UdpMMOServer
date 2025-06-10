package io.shrouded.recievers.connect;

import io.shrouded.recievers.PayloadMessageRequest;
import jakarta.validation.constraints.NotNull;

public record ConnectMessageRequest(@NotNull String jwt) implements PayloadMessageRequest { }
