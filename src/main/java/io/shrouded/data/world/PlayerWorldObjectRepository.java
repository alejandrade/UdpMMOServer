package io.shrouded.data.world;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PlayerWorldObjectRepository {
    private final WorldObjectRepository worldObjectRepository;

    public Mono<PlayerWorldObject> save(final PlayerWorldObject player) {
        return worldObjectRepository.save(player);
    }

    public Mono<PlayerWorldObject> findById(final WorldObjectId id) {
        return worldObjectRepository.findById(id, PlayerWorldObject.class);
    }

    public Mono<Boolean> remove(final WorldObjectId id, final double x, final double z) {
        return worldObjectRepository.remove(id, x, z);
    }

    public Flux<PlayerWorldObject> findAll() {
        return worldObjectRepository.findAll(PlayerWorldObject.class);
    }

    public Flux<PlayerWorldObject> findNearby(final double x, final double z, final double radius) {
        return worldObjectRepository.findNearby(x, z, radius, PlayerWorldObject.class);
    }

}
