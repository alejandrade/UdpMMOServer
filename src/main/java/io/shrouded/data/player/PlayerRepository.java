package io.shrouded.data.player;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PlayerRepository extends ReactiveMongoRepository<Player, String> {
}
