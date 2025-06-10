package io.shrouded.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.shrouded.util.MDCUtil;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class CacheConfig {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;
    private Scheduler blockingScheduler;

    @Bean
    public RedisClient redisClient(
            @Value("${redis.host:localhost}") String host,
            @Value("${redis.port:6379}") int port,
            @Value("${redis.password:}") String password,
            @Value("${redis.database:0}") int database
    ) {
        RedisURI uri = RedisURI.Builder.redis(host, port)
                .withDatabase(database)
                .withPassword(password.isEmpty() ? null : password.toCharArray())
                .build();

        RedisClient client = RedisClient.create(uri);

        // Test connection at startup
        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            connection.sync().ping(); // Will throw if connection fails
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to connect to Redis at startup", ex);
        }

        return client;
    }
    @Bean
    public Scheduler defaultBlockingScheduler(
            @Value("${reactor.blocking-scheduler.max-threads:100}") int maxThreads,
            @Value("${reactor.blocking-scheduler.max-queue-size:100000}") int maxQueueSize,
            @Value("${reactor.blocking-scheduler.thread-name-prefix:reactor-worker}") String threadNamePrefix,
            @Value("${reactor.blocking-scheduler.ttl-seconds:60}") int ttlSeconds
    ) {
        this.blockingScheduler = Schedulers.newBoundedElastic(maxThreads, maxQueueSize,
                MDCUtil.mdcThreadFactory(threadNamePrefix), ttlSeconds);
        blockingScheduler.init();
        return this.blockingScheduler;
    }

    @PreDestroy
    public void shutdownResources() {
        if (redisConnection != null) {
            redisConnection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
        if (blockingScheduler != null) {
            blockingScheduler.dispose();
        }
    }
}
