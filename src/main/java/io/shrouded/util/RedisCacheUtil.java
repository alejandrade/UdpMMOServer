package io.shrouded.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheUtil {
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;

    private StatefulRedisConnection<String, String> connection;
    private RedisReactiveCommands<String, String> reactive;

    @PostConstruct
    public void init() {
        this.connection = redisClient.connect();
        this.reactive = connection.reactive();
    }

    public <T> Mono<Void> set(String key, T value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            return reactive.setex(key, ttl.getSeconds(), json).then();
        } catch (JsonProcessingException e) {
            log.error("Serialization failed for key '{}'", key, e);
            return Mono.empty();
        }
    }

    public <T> Mono<T> get(String key, Class<T> type) {
        return reactive.get(key)
                .flatMap(json -> {
                    try {
                        return Mono.justOrEmpty(objectMapper.readValue(json, type));
                    } catch (IOException e) {
                        log.error("Deserialization failed for key '{}'", key, e);
                        return Mono.empty();
                    }
                });
    }

    public Mono<Void> delete(String key) {
        return reactive.del(key).then();
    }

    @PreDestroy
    public void shutdown() {
        if (connection != null) connection.close();
    }
}
