package io.shrouded.recievers;

import io.shrouded.recievers.connect.ConnectReceiver;
import io.shrouded.recievers.move.MoveReceiver;
import io.shrouded.recievers.ping.PingReceiver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RequestMessageType {
    connect(ConnectReceiver.class),
    ping(PingReceiver.class),
    move(MoveReceiver.class);

    private final Class<? extends MessageReceiver> aClass;
}
