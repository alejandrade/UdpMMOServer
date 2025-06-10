package io.shrouded.data.entity;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class SqlExecutor {

    private final Pool pool;
    private final Scheduler scheduler;

    public <T> Mono<T> query(String sql, Tuple params, String context, Function<RowSet<Row>, T> mapper) {
        return Mono.fromCompletionStage(
                        pool.preparedQuery(sql).execute(params).toCompletionStage()
                )
                .map(mapper)
                .doOnError(e -> log.error("Query failed [{}]: SQL='{}' Params={} Error={}", context, sql, params, e.toString()))
                .subscribeOn(scheduler);
    }

    public Mono<Boolean> executeUpdate(String sql, Tuple params, String context) {
        return Mono.fromCompletionStage(
                        pool.preparedQuery(sql).execute(params).toCompletionStage()
                )
                .map(rs -> rs.rowCount() > 0)
                .onErrorResume(e -> {
                    log.error("Update failed [{}]: SQL='{}' Params={} Error={}", context, sql, params, e.toString());
                    return Mono.just(false);
                });
    }
}
