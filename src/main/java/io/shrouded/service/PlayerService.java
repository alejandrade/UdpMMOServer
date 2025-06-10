package io.shrouded.service;

import io.shrouded.data.entity.player.PlayerEntity;
import io.shrouded.data.entity.player.PlayerId;
import io.shrouded.data.entity.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Mono<PlayerEntity> save(PlayerId id) {
        return playerRepository.read(id)
                .onErrorResume(e -> {
                    log.warn("Player not found, attempting to create. Reason: {}", e.toString());
                    return playerRepository.create(id)
                            .flatMap(success -> {
                                if (success) {
                                    return Mono.just(new PlayerEntity(id));
                                } else {
                                    return Mono.error(new RuntimeException("Failed to create player"));
                                }
                            });
                });
    }

}
