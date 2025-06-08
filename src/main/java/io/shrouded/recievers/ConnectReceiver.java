package io.shrouded.recievers;

import io.shrouded.udpConnectionHelper;
import io.shrouded.data.player.PlayerService;
import io.shrouded.recievers.request.ConnectMessageRequest;
import io.shrouded.recievers.response.BaseResponse;
import io.shrouded.recievers.response.PayloadMessageResponse;
import io.shrouded.recievers.response.ConnectMessageResponse;
import io.shrouded.recievers.response.ResponseMessageType;
import io.shrouded.util.ConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectReceiver implements MessageReceiver<ConnectMessageRequest, ConnectMessageResponse> {

    private final PlayerService playerService;
    private final ConnectionManager connectionManager;

    @Override
    @SneakyThrows
    public void handle(final String requestId,
                       final ConnectMessageRequest payloadMessage,
                       final udpConnectionHelper publisherHelper) {
        playerService.create(payloadMessage.playerName())
                .doOnNext(saved -> log.info("Created player with ID: {}", saved.getId()))
                .subscribe((player) ->{
                    connectionManager.register(player.getPlayerId(), publisherHelper.getSender());
                });

        publisherHelper.respondSender(new BaseResponse<PayloadMessageResponse>(requestId,
                Instant.now(),
                ResponseMessageType.registerResponse, 200, "registered", new ConnectMessageResponse()));
    }
}
