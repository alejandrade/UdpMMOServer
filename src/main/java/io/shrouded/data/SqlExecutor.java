package io.shrouded.data;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.shrouded.config.CacheKey;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class SqlExecutor {

    private final Pool pool;
    private final HazelcastInstance hazelcastInstance;
    private final Scheduler scheduler;

    private IMap<String, Object> getCache() {
        return hazelcastInstance.getMap(CacheKey.SQL_QUERY_CACHE);
    }

    private String makeCacheKey(String sql, Tuple params) {
        return sql + "::" + params;
    }

    public <T> Mono<T> queryAndCache(String sql, Tuple params, String context, Function<RowSet<Row>, T> mapper) {
        String key = makeCacheKey(sql, params);
        return Mono.defer(() -> {
            T cached = (T) getCache().get(key);
            if (cached != null) {
                return Mono.just(cached);
            }

            return Mono.fromCompletionStage(
                            pool.preparedQuery(sql).execute(params).toCompletionStage()
                    )
                    .map(mapper)
                    .doOnNext(mapped -> getCache().put(key, mapped, 60, TimeUnit.SECONDS))
                    .doOnError(e -> log.error("Query failed [{}]: SQL='{}' Params={} Error={}", context, sql, params, e.toString()));
        }).subscribeOn(scheduler);
    }

    public Mono<Boolean> executeUpdate(String sql, Tuple params, String context) {
        return Mono.fromCompletionStage(
                        pool.preparedQuery(sql)
                                .execute(params)
                                .toCompletionStage()
                )
                .map(rs -> rs.rowCount() > 0)
                .onErrorResume(e -> {
                    log.error("Update failed [{}]: SQL='{}' Params={} Error={}", context, sql, params, e.toString());
                    return Mono.just(false);
                });
    }
}
