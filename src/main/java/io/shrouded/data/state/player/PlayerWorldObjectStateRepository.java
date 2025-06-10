package io.shrouded.data.state.player;

import io.shrouded.data.state.WorldObjectStateId;
import io.shrouded.data.state.WorldObjectStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PlayerWorldObjectStateRepository {
    private final WorldObjectStateRepository worldObjectStateRepository;

    public Mono<PlayerWorldObjectState> save(final PlayerWorldObjectState player) {
        return worldObjectStateRepository.save(player);
    }

    public Mono<PlayerWorldObjectState> findById(final WorldObjectStateId id) {
        return worldObjectStateRepository.findById(id, PlayerWorldObjectState.class);
    }

    public Mono<Boolean> remove(final WorldObjectStateId id, final double x, final double z) {
        return worldObjectStateRepository.remove(id, x, z);
    }

    public Flux<PlayerWorldObjectState> findAll() {
        return worldObjectStateRepository.findAll(PlayerWorldObjectState.class);
    }

    public Flux<PlayerWorldObjectState> findNearby(final double x, final double z, final double radius) {
        return worldObjectStateRepository.findNearby(x, z, radius, PlayerWorldObjectState.class);
    }

}
