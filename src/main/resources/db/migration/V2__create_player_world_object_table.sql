CREATE TABLE player_world_object (
    player_id VARCHAR(36) NOT NULL,                    -- worldObjectId

    position_x FLOAT NOT NULL,
    position_y FLOAT NOT NULL,
    position_z FLOAT NOT NULL,

    rotation_x FLOAT NOT NULL,
    rotation_y FLOAT NOT NULL,
    rotation_z FLOAT NOT NULL,
    rotation_w FLOAT NOT NULL,

    velocity_x FLOAT NOT NULL,
    velocity_y FLOAT NOT NULL,
    velocity_z FLOAT NOT NULL,

    health INT NOT NULL,
    energy INT NOT NULL,

    is_dead BOOLEAN NOT NULL DEFAULT FALSE,
    is_in_combat BOOLEAN NOT NULL DEFAULT FALSE,

    cast_spell_id INT,                          -- nullable when not casting
    target_id VARCHAR(36),                      -- nullable

    active_buffs JSONB NOT NULL DEFAULT '[]',
    active_debuffs JSONB NOT NULL DEFAULT '[]',

    PRIMARY KEY (player_id)
);
