package io.shrouded.data.state;

import lombok.RequiredArgsConstructor;
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
public final class WorldObjectStateRepository {

    private static final int CELL_SIZE = 100;

    private final Scheduler scheduler;

    private final ConcurrentHashMap<WorldObjectStateId, WorldObject> worldObjectMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<GridCellId, Set<WorldObjectStateId>> gridMap = new ConcurrentHashMap<>();

    public <T extends WorldObject & HasWorldCoordinates> Mono<T> save(final T value) {
        if (value == null || value.getId() == null) {
            throw new IllegalArgumentException("Cannot save null object or object with null ID");
        }

        final WorldObjectStateId id = value.getId();
        return Mono.fromSupplier(() -> {
            worldObjectMap.put(id, value);
            final GridCellId cell = GridCellId.fromCoordinates(value.getX(), value.getZ(), CELL_SIZE);
            gridMap.compute(cell, (k, v) -> {
                if (v == null) v = ConcurrentHashMap.newKeySet();
                v.add(id);
                return v;
            });
            return value;
        }).subscribeOn(scheduler);
    }

    public <T extends WorldObject> Mono<T> findById(final WorldObjectStateId id, final Class<T> type) {
        return Mono.fromCallable(() -> worldObjectMap.get(id))
                .map(obj -> obj.ofType(type))
                .subscribeOn(scheduler);
    }

    public Mono<Boolean> remove(final WorldObjectStateId id, final double x, final double z) {
        return Mono.fromSupplier(() -> {
            final GridCellId cell = GridCellId.fromCoordinates(x, z, CELL_SIZE);
            final Set<WorldObjectStateId> set = gridMap.get(cell);
            if (set != null) set.remove(id);
            return worldObjectMap.remove(id) != null;
        }).subscribeOn(scheduler);
    }

    public <T extends WorldObject> Flux<T> findAll(final Class<T> type) {
        return Flux.defer(() -> Flux.fromIterable(worldObjectMap.values()))
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

        return Mono.fromSupplier(() -> {
                    Set<WorldObjectStateId> ids = queryCells.stream()
                            .map(gridMap::get)
                            .flatMap(Set::stream)
                            .collect(Collectors.toSet());

                    return ids.stream()
                            .map(worldObjectMap::get)
                            .map(obj -> obj.ofType(type))
                            .collect(Collectors.toList());
                }).flatMapMany(Flux::fromIterable)
                .subscribeOn(scheduler);
    }
}
