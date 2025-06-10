package io.shrouded.data.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SqlTemplates {
    CREATE_PLAYER("INSERT INTO player (id) VALUES ($1)"),
    READ_PLAYER("SELECT id FROM player WHERE id = $1"),
    UPDATE_PLAYER("UPDATE player SET name = $1 WHERE id = $2"),
    DELETE_PLAYER("DELETE FROM player WHERE id = $1"),
    PLAYER_EXISTS("SELECT 1 FROM player WHERE id = $1 LIMIT 1"),
    UPSERT_PLAYER_WORLD_OBJECT("""
        INSERT INTO player_world_object (
            id,
            position_x, position_y, position_z,
            rotation_x, rotation_y, rotation_z, rotation_w,
            velocity_x, velocity_y, velocity_z
        ) VALUES (
            $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11
        )
        ON CONFLICT (id) DO UPDATE SET
            position_x = EXCLUDED.position_x,
            position_y = EXCLUDED.position_y,
            position_z = EXCLUDED.position_z,
            rotation_x = EXCLUDED.rotation_x,
            rotation_y = EXCLUDED.rotation_y,
            rotation_z = EXCLUDED.rotation_z,
            rotation_w = EXCLUDED.rotation_w,
            velocity_x = EXCLUDED.velocity_x,
            velocity_y = EXCLUDED.velocity_y,
            velocity_z = EXCLUDED.velocity_z
    """);

    ;

    private final String sql;

    public String getSql() {
        return sql;
    }
}
