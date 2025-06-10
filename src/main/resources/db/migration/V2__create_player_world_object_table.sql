CREATE TABLE player_world_object (
    id VARCHAR(36) NOT NULL,                    -- worldObjectId

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

    PRIMARY KEY (id)
);
