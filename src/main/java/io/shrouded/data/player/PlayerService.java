package io.shrouded.data.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Mono<Player> create(String name) {
        Player player = new Player();
        player.setName(name);
        return playerRepository.save(player);
    }
}
