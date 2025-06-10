package io.shrouded.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.shrouded.data.world.WorldObject;
import io.shrouded.data.world.WorldObjectId;
import io.shrouded.data.world.GridCellId;
import io.shrouded.util.MDCUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Set;

@Configuration
public class CacheConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setInstanceName("game-instance");

        // Default configuration applied to all maps unless overridden
        config.getMapConfig("default")
                .setTimeToLiveSeconds(0) // no TTL
                .setBackupCount(1);      // 1 backup copy per entry

        config.addMapConfig(new MapConfig(CacheKey.GRID_SPATIAL_INDEX_CACHE)
                .setTimeToLiveSeconds(0)
                .setBackupCount(1));

        config.addMapConfig(new MapConfig(CacheKey.VOLATILE_CACHE)
                .setTimeToLiveSeconds(0)
                .setBackupCount(1));

        config.addMapConfig(new MapConfig(CacheKey.SQL_QUERY_CACHE)
                .setTimeToLiveSeconds(600));

        return Hazelcast.newHazelcastInstance(config);
    }


    @Bean
    public Scheduler defaultBlockingScheduler(
            @Value("${reactor.blocking-scheduler.max-threads:100}") int maxThreads,
            @Value("${reactor.blocking-scheduler.max-queue-size:100000}") int maxQueueSize,
            @Value("${reactor.blocking-scheduler.thread-name-prefix:reactor-worker}") String threadNamePrefix,
            @Value("${reactor.blocking-scheduler.ttl-seconds:60}") int ttlSeconds
    ) {
        Scheduler scheduler = Schedulers.newBoundedElastic(maxThreads, maxQueueSize,
                MDCUtil.mdcThreadFactory(threadNamePrefix), ttlSeconds);
        scheduler.init();
        return scheduler;
    }

    @Bean
    @Qualifier("gridSpatialIndex")
    public IMap<GridCellId, Set<WorldObjectId>> gridSpatialIndex(HazelcastInstance instance) {
        return instance.getMap(CacheKey.GRID_SPATIAL_INDEX_CACHE);
    }

    @Bean
    @Qualifier("volatileCache")
    public IMap<WorldObjectId, WorldObject> volatileCache(HazelcastInstance instance) {
        return instance.getMap(CacheKey.VOLATILE_CACHE);
    }
}
