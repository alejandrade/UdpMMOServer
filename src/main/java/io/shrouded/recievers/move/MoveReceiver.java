package io.shrouded.recievers.move;

import io.shrouded.data.world.PlayerWorldObject;
import io.shrouded.data.world.PlayerWorldObjectRepository;
import io.shrouded.data.world.WorldObjectId;
import io.shrouded.recievers.MessageReceiver;
import io.shrouded.recievers.BaseResponse;
import io.shrouded.recievers.DefaultMessageResponse;
import io.shrouded.recievers.PayloadMessageResponse;
import io.shrouded.recievers.ResponseMessageType;
import io.shrouded.UdpConnectionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoveReceiver implements MessageReceiver<MoveRequest, DefaultMessageResponse> {
    private final PlayerWorldObjectRepository playerWorldObjectRepository;

    @Override
    public void handle(final String requestId,
                       final MoveRequest moveRequest,
                       final UdpConnectionHelper publisherHelper) {
        final String playerId = publisherHelper.getUser().id().value();
        final WorldObjectId id = new WorldObjectId(playerId);
        final PlayerWorldObject updated = new PlayerWorldObject(
                id,
                moveRequest.position(),
                moveRequest.rotation(),
                moveRequest.velocity()
        );
        playerWorldObjectRepository.save(updated).subscribe((playerWorldObject) -> {
            publisherHelper.respondSender(new BaseResponse<PayloadMessageResponse>(requestId,
                    Instant.now(),
                    ResponseMessageType.debug, 200, "saved move", new DefaultMessageResponse()));
        });
    }


    @Override
    public boolean isPrivate() {
        return true;
    }
}
