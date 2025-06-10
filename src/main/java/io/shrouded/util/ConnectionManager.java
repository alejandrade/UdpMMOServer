package io.shrouded.util;

import io.shrouded.data.entity.player.PlayerId;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@Service
public class ConnectionManager {
    private final ConcurrentHashMap<PlayerId, RegisteredClient> usersById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<InetSocketAddress, PlayerId> idByAddress = new ConcurrentHashMap<>();
    private final Duration expirationDuration = Duration.ofMinutes(2);

    public void addConnection(PlayerId playerId, InetSocketAddress address) {
        RegisteredClient client = new RegisteredClient(playerId, address, Instant.now());
        usersById.put(playerId, client);
        idByAddress.put(address, playerId);
    }

    public boolean touch(PlayerId playerId) {
        if (playerId == null) return false;
        return usersById.computeIfPresent(playerId, (id, user) -> user.touch()) != null;
    }

    public boolean touch(InetSocketAddress address) {
        if (address == null) return false;
        RegisteredClient userByAddress = getUserByAddress(address);
        if (userByAddress == null) return false;
        return touch(userByAddress.id());
    }

    public RegisteredClient getUserByAddress(InetSocketAddress address) {
        PlayerId id = idByAddress.get(address);
        return id != null ? usersById.get(id) : null;
    }

    public boolean userLoggedIn(InetSocketAddress address) {
        return getUserByAddress(address) != null;
    }

    public RegisteredClient getUser(PlayerId playerId) {
        return usersById.get(playerId);
    }


    public void remove(PlayerId playerId) {
        RegisteredClient client = usersById.remove(playerId);
        if (client != null) {
            idByAddress.remove(client.address());
        }
    }

    public Map<PlayerId, RegisteredClient> getAll() {
        return Map.copyOf(usersById);
    }

    public void forEach(BiConsumer<PlayerId, RegisteredClient> consumer) {
        usersById.forEach(consumer);
    }

    @Scheduled(fixedRate = 30000)
    public void cleanupExpiredUsers() {
        Instant cutoff = Instant.now().minus(expirationDuration);
        usersById.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().lastSeen().isBefore(cutoff);
            if (expired) {
                idByAddress.remove(entry.getValue().address());
            }
            return expired;
        });
    }
}
