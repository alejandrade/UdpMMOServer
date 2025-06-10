package io.shrouded.recievers.ping;

import io.shrouded.UdpConnectionHelper;
import io.shrouded.recievers.MessageReceiver;
import io.shrouded.recievers.BaseResponse;
import io.shrouded.recievers.ResponseMessageType;
import io.shrouded.util.ConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PingReceiver implements MessageReceiver<PingMessageRequest, PongMessageResponse> {

    private final ConnectionManager connectionManager;

    @Override
    public void handle(final String requestId,
                       final PingMessageRequest payloadMessage,
                       final UdpConnectionHelper publisherHelper) {
        publisherHelper.respondSender(new BaseResponse<>(requestId,
                Instant.now(),
                ResponseMessageType.pong,
                200,
                "pong",
                new PongMessageResponse(connectionManager.touch(publisherHelper.getSocketAddress()))
        ));
    }

    @Override
    public boolean isPrivate() {
        return false;
    }
}
