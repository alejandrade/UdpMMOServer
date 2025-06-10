package io.shrouded.data.entity.player;

import io.shrouded.data.entity.SqlExecutor;
import io.shrouded.data.entity.SqlTemplates;
import io.shrouded.util.RedisCacheUtil;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlayerRepository {

    private final SqlExecutor sqlExecutor;
    private final RedisCacheUtil redisCacheUtil;

    @Value("${cache.player.ttl:1h}")
    private Duration playerCacheTtl;

    public Mono<Boolean> create(PlayerId id) {
        String sql = SqlTemplates.CREATE_PLAYER.getSql();
        return sqlExecutor.executeUpdate(sql, Tuple.of(id), "Create Player");
    }

    public Mono<PlayerEntity> read(PlayerId id) {
        String cacheKey = "player:read:" + id;

        return redisCacheUtil.get(cacheKey, PlayerEntity.class)
                .switchIfEmpty(Mono.defer(() -> {
                    String sql = SqlTemplates.READ_PLAYER.getSql();
                    return sqlExecutor.query(
                            sql,
                            Tuple.of(id),
                            "Read Player",
                            rs -> {
                                if (!rs.iterator().hasNext()) {
                                    throw new NoSuchElementException("Player with id '" + id + "' not found");
                                }
                                Row row = rs.iterator().next();
                                return new PlayerEntity(PlayerId.of(row.getString("id")));
                            }
                    ).flatMap(player -> redisCacheUtil.set(cacheKey, player, playerCacheTtl).thenReturn(player));
                }));
    }

    public Mono<Boolean> delete(PlayerId id) {
        String sql = SqlTemplates.DELETE_PLAYER.getSql();
        return sqlExecutor.executeUpdate(sql, Tuple.of(id), "Delete Player")
                .flatMap(success -> {
                    if (success) {
                        return Mono.when(
                                redisCacheUtil.delete("player:read:" + id)
                        ).thenReturn(true);
                    }
                    return Mono.just(false);
                });
    }

    public Mono<Boolean> exists(PlayerId id) {
        return read(id)
                .map(p -> true)
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(false));
    }
}
