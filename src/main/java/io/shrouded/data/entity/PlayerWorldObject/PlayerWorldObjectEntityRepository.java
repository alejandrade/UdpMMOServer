package io.shrouded.data.entity.PlayerWorldObject;

import io.shrouded.data.entity.SqlExecutor;
import io.shrouded.data.entity.SqlTemplates;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlayerWorldObjectEntityRepository {

    private final SqlExecutor sqlExecutor;

    public Mono<Boolean> upsert(PlayerWorldObjectEntity obj) {
        String sql = SqlTemplates.UPSERT_PLAYER_WORLD_OBJECT.getSql();
        return sqlExecutor.executeUpdate(
                sql,
                Tuple.of(
                        obj.id().value(),
                        obj.positionX(), obj.positionY(), obj.positionZ(),
                        obj.rotationX(), obj.rotationY(), obj.rotationZ(), obj.rotationW(),
                        obj.velocityX(), obj.velocityY(), obj.velocityZ()
                ),
                "Upsert PlayerWorldObject"
        );
    }
}
