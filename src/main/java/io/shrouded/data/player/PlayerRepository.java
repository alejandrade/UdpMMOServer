package io.shrouded.data.player;

import io.shrouded.data.SqlExecutor;
import io.shrouded.data.SqlTemplates;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlayerRepository {

    private final SqlExecutor sqlExecutor;

    public Mono<Boolean> create(String id) {
        String sql = SqlTemplates.CREATE_PLAYER.getSql();
        return sqlExecutor.executeUpdate(sql, Tuple.of(id), "Create Player");
    }

    public Mono<Player> read(String id) {
        String sql = SqlTemplates.READ_PLAYER.getSql();
        return sqlExecutor.queryAndCache(
                sql,
                Tuple.of(id),
                "Read Player",
                rs -> {
                    if (!rs.iterator().hasNext()) {
                        throw new NoSuchElementException("Player with id '" + id + "' not found");
                    }
                    Row row = rs.iterator().next();
                    return new Player(PlayerId.of(row.getString("id")));
                }
        );
    }

    public Mono<Boolean> delete(String id) {
        String sql = SqlTemplates.DELETE_PLAYER.getSql();
        return sqlExecutor.executeUpdate(sql, Tuple.of(id), "Delete Player");
    }

    public Mono<Boolean> exists(String id) {
        String sql = SqlTemplates.PLAYER_EXISTS.getSql();
        return sqlExecutor.queryAndCache(
                sql,
                Tuple.of(id),
                "Exists Player",
                rs -> rs.iterator().hasNext()
        );
    }
}
