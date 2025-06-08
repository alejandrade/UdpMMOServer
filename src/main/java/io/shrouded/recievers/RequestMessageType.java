package io.shrouded.recievers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RequestMessageType {
    connect(ConnectReceiver.class),
    ping(PingReceiver.class);

    private final Class<? extends MessageReceiver> aClass;
}
