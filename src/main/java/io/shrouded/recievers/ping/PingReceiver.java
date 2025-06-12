package io.shrouded.recievers.ping;

import io.shrouded.UdpConnectionHelper;
import io.shrouded.recievers.EmptyRequest;
import io.shrouded.recievers.MessageReceiver;
import io.shrouded.recievers.BaseResponse;
import io.shrouded.recievers.ResponseMessageType;
import io.shrouded.util.ConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PingReceiver implements MessageReceiver<EmptyRequest, PongMessageResponse> {

    private final ConnectionManager connectionManager;

    @Override
    public void handle(final String requestId,
                       final EmptyRequest emptyRequest,
                       final UdpConnectionHelper publisherHelper) {
        publisherHelper.respondSender(new BaseResponse<>(
                ResponseMessageType.pong,
                new PongMessageResponse(connectionManager.touch(publisherHelper.getSocketAddress()))
        ));
    }

    @Override
    public boolean isPrivate() {
        return false;
    }
}
