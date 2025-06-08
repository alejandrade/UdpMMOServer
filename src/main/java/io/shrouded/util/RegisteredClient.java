package io.shrouded.util;

import io.shrouded.data.player.PlayerId;

import java.net.InetSocketAddress;
import java.time.Instant;

public record RegisteredClient(PlayerId id, InetSocketAddress address, Instant lastSeen) {
    public RegisteredClient touch() {
        return new RegisteredClient(id, address, Instant.now());
    }
}
