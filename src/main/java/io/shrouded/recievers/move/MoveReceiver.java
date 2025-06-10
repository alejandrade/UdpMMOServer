package io.shrouded.recievers.move;

import io.shrouded.data.entity.PlayerWorldObject.PlayerWorldObjectEntity;
import io.shrouded.data.state.player.PlayerWorldObjectState;
import io.shrouded.data.state.player.PlayerWorldObjectStateRepository;
import io.shrouded.data.entity.PlayerWorldObject.PlayerWorldObjectEntityRepository;
import io.shrouded.data.state.WorldObjectStateId;
import io.shrouded.recievers.MessageReceiver;
import io.shrouded.recievers.BaseResponse;
import io.shrouded.recievers.DefaultMessageResponse;
import io.shrouded.recievers.ResponseMessageType;
import io.shrouded.UdpConnectionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoveReceiver implements MessageReceiver<MoveRequest, DefaultMessageResponse> {

    private final PlayerWorldObjectEntityRepository entityRepository;
    private final PlayerWorldObjectStateRepository stateRepository;
    private final Scheduler scheduler;

    @Override
    public void handle(final String requestId,
                       final MoveRequest moveRequest,
                       final UdpConnectionHelper publisherHelper) {

        final String playerId = publisherHelper.getUser().id().value();
        final WorldObjectStateId worldObjectId = new WorldObjectStateId(playerId);
        final Instant now = Instant.now();

        final PlayerWorldObjectState newState = new PlayerWorldObjectState(
                worldObjectId,
                moveRequest.position(),
                moveRequest.rotation(),
                moveRequest.velocity(),
                now
        );

        stateRepository.findById(worldObjectId)
                .switchIfEmpty(Mono.just(newState))
                .flatMap(currentState -> {
                    // Step 2: Save immediately to in-memory map
                    boolean isFirstTime = currentState == newState;
                    return stateRepository.save(PlayerWorldObjectState.cloneWithDifferentUpdate(newState,
                            isFirstTime ? Instant.now() : currentState.getLastUpdate()));
                })
                .flatMap(previousState -> {
                    // Step 3: After save, check if we should persist
                    boolean isStale = previousState.getLastUpdate()
                            .plus(5, ChronoUnit.SECONDS)
                            .isBefore(newState.getLastUpdate());
                    if (isStale) {
                        PlayerWorldObjectEntity entity = PlayerWorldObjectEntity.from(newState);
                        entityRepository.upsert(entity)
                                .doOnError(err -> log.error("Async persist failed", err))
                                .subscribeOn(scheduler)
                                .subscribe();
                        return stateRepository.save(PlayerWorldObjectState.cloneWithDifferentUpdate(newState, Instant.now()));
                    } else {
                        return Mono.just(previousState);
                    }
                })
                .doOnSuccess(previousState -> {
                    publisherHelper.respondSender(
                            new BaseResponse<>(
                                    requestId,
                                    previousState.lastSavedUpdate(),
                                    ResponseMessageType.debug,
                                    200,
                                    "saved move",
                                    new DefaultMessageResponse()
                            )
                    );
                })
                .doOnError(err -> log.error("Failed to handle move", err))
                .subscribeOn(scheduler)
                .subscribe();;

    }

    @Override
    public boolean isPrivate() {
        return true;
    }
}
