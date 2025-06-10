package io.shrouded.data.world;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class GridCellId {
    private final int cellX;
    private final int cellZ;

    public static GridCellId fromCoordinates(double x, double z, int cellSize) {
        return new GridCellId((int) (x / cellSize), (int) (z / cellSize));
    }
}

