package io.shrouded.recievers;

import io.shrouded.udpConnectionHelper;
import io.shrouded.recievers.request.PingMessageRequest;
import io.shrouded.recievers.response.BaseResponse;
import io.shrouded.recievers.response.PongMessageResponse;
import io.shrouded.recievers.response.ResponseMessageType;
import io.shrouded.util.ConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PingReceiver implements MessageReceiver<PingMessageRequest, PongMessageResponse> {

    private final ConnectionManager connectionManager;

    @SneakyThrows
    @Override
    public void handle(final String requestId,
                       final PingMessageRequest payloadMessage,
                       final udpConnectionHelper publisherHelper) {
        publisherHelper.respondSender(new BaseResponse<>(requestId,
                Instant.now(),
                ResponseMessageType.pong,
                200,
                "pong",
                new PongMessageResponse(connectionManager.touch(publisherHelper.getSender()))
        ));
    }
}
