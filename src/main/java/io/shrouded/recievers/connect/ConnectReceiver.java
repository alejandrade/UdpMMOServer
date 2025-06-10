package io.shrouded.recievers.connect;

import io.shrouded.data.entity.player.PlayerId;
import io.shrouded.recievers.MessageReceiver;
import io.shrouded.service.PlayerService;
import io.shrouded.UdpConnectionHelper;
import io.shrouded.recievers.BaseResponse;
import io.shrouded.recievers.PayloadMessageResponse;
import io.shrouded.recievers.DefaultMessageResponse;
import io.shrouded.recievers.ResponseMessageType;
import io.shrouded.util.ConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Scheduler;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectReceiver implements MessageReceiver<ConnectMessageRequest, DefaultMessageResponse> {

    private final PlayerService playerService;
    private final ConnectionManager connectionManager;
    private final Scheduler scheduler;

    @Override
    public void handle(final String requestId,
                       final ConnectMessageRequest payloadMessage,
                       final UdpConnectionHelper publisherHelper) {
        if (publisherHelper.isConnected()) {
            publisherHelper.respondSender(new BaseResponse<PayloadMessageResponse>(requestId,
                    Instant.now(),
                    ResponseMessageType.success, 200, "registered", new DefaultMessageResponse()));
        } else {
            playerService.save(new PlayerId("id"))
                    .doOnNext(saved -> log.info("Created player with ID: {}", saved.getId()))
                    .subscribeOn(scheduler)
                    .subscribe((player) -> {
                        connectionManager.addConnection(player.getId(), publisherHelper.getSocketAddress());
                        publisherHelper.respondSender(new BaseResponse<PayloadMessageResponse>(requestId,
                                Instant.now(),
                                ResponseMessageType.success, 200, "registered", new DefaultMessageResponse()));
                    });
        }
    }

    @Override
    public boolean isPrivate() {
        return false;
    }
}
