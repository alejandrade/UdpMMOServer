package io.shrouded.config;

import io.shrouded.util.MDCUtil;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadFactory;

@Configuration
public class VertxDatabaseConfig {

    @Bean
    public Pool pgPool(Vertx vertx,
                       @Value("${vertx.pg.host}") String host,
                       @Value("${vertx.pg.port}") int port,
                       @Value("${vertx.pg.database}") String db,
                       @Value("${vertx.pg.username}") String user,
                       @Value("${vertx.pg.password}") String pass,
                       @Value("${vertx.pg.max-pool-size}") int maxPoolSize) throws Exception {
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(port)
                .setHost(host)
                .setDatabase(db)
                .setUser(user)
                .setPassword(pass)
                .setSsl(false)
                .setConnectTimeout(3000); // milliseconds;

        PoolOptions poolOptions = new PoolOptions().setMaxSize(maxPoolSize);

        Pool pool = PgBuilder.pool()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(vertx)
                .build();

        // ✅ Check connectivity at startup
        pool.getConnection()
                .toCompletionStage()
                .toCompletableFuture()
                .get(5, java.util.concurrent.TimeUnit.SECONDS);

        return pool;
    }

    @Bean
    public Vertx vertx() {
        // You could also use a custom thread pool if needed
        VertxOptions options = new VertxOptions()
                .setWorkerPoolSize(10);

        return Vertx.vertx(options);
    }

}
