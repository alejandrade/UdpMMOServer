package io.shrouded.data;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SqlTemplates {
    CREATE_PLAYER("INSERT INTO player (id) VALUES ($1)"),
    READ_PLAYER("SELECT id FROM player WHERE id = $1"),
    UPDATE_PLAYER("UPDATE player SET name = $1 WHERE id = $2"),
    DELETE_PLAYER("DELETE FROM player WHERE id = $1"),
    PLAYER_EXISTS("SELECT 1 FROM player WHERE id = $1 LIMIT 1");

    private final String sql;

    public String getSql() {
        return sql;
    }
}
