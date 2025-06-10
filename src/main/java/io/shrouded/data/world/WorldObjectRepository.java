package io.shrouded.data.world;

import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
final class WorldObjectRepository {

    private static final int CELL_SIZE = 100;

    private final Scheduler scheduler;

    @Qualifier("volatileCache")
    private final IMap<WorldObjectId, WorldObject> volatileCache;

    @Qualifier("gridSpatialIndex")
    private final IMap<GridCellId, Set<WorldObjectId>> gridMap;

    public <T extends WorldObject & HasWorldCoordinates> Mono<T> save(final T value) {
        if (value == null || value.getId() == null) {
            throw new IllegalArgumentException("Cannot save null object or object with null ID");
        }

        final WorldObjectId id = value.getId();
        return Mono.fromSupplier(() -> {
            volatileCache.put(id, value);
            final GridCellId cell = GridCellId.fromCoordinates(value.getX(), value.getZ(), CELL_SIZE);
            gridMap.compute(cell, (k, v) -> {
                if (v == null) v = ConcurrentHashMap.newKeySet();
                v.add(id);
                return v;
            });
            return value;
        }).subscribeOn(scheduler);
    }

    public <T extends WorldObject> Mono<T> findById(final WorldObjectId id, final Class<T> type) {
        return Mono.fromCallable(() -> volatileCache.get(id))
                .map(obj -> obj.ofType(type))
                .subscribeOn(scheduler);
    }

    public Mono<Boolean> remove(final WorldObjectId id, final double x, final double z) {
        return Mono.fromSupplier(() -> {
            final GridCellId cell = GridCellId.fromCoordinates(x, z, CELL_SIZE);
            final Set<WorldObjectId> set = gridMap.get(cell);
            if (set != null) set.remove(id);
            return volatileCache.remove(id) != null;
        }).subscribeOn(scheduler);
    }

    public <T extends WorldObject> Flux<T> findAll(final Class<T> type) {
        return Flux.defer(() -> Flux.fromIterable(volatileCache.values()))
                .map(obj -> obj.ofType(type))
                .subscribeOn(scheduler);
    }

    public <T extends WorldObject> Flux<T> findNearby(final double x, final double z, final double radius, final Class<T> type) {
        final int cells = (int) Math.ceil(radius / CELL_SIZE);
        final GridCellId center = GridCellId.fromCoordinates(x, z, CELL_SIZE);
        final Set<GridCellId> queryCells = new HashSet<>();
        for (int dx = -cells; dx <= cells; dx++) {
            for (int dz = -cells; dz <= cells; dz++) {
                queryCells.add(new GridCellId(center.getCellX() + dx, center.getCellZ() + dz));
            }
        }

        return Mono.fromCallable(() -> gridMap.getAll(queryCells))
                .flatMapMany(allResults -> {
                    Set<WorldObjectId> ids = allResults.values().stream()
                            .flatMap(Set::stream)
                            .collect(Collectors.toSet());
                    return Flux.fromIterable(ids)
                            .map(volatileCache::get)
                            .map(obj -> obj.ofType(type));
                })
                .subscribeOn(scheduler);
    }
}

