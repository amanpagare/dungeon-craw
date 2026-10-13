package dev.games.dungeon;

import java.util.Arrays;

/** A rectangular grid of tiles. */
public class DungeonMap {
    private final int width;
    private final int height;
    private final Tile[][] tiles;

    public DungeonMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Map dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
        for (Tile[] row : tiles) {
            Arrays.fill(row, Tile.WALL);
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean inBounds(Position p) {
        return p.x() >= 0 && p.y() >= 0 && p.x() < width && p.y() < height;
    }

    public Tile get(Position p) {
        if (!inBounds(p)) {
            return Tile.WALL;
        }
        return tiles[p.y()][p.x()];
    }

    public void set(Position p, Tile tile) {
        if (!inBounds(p)) {
            throw new IndexOutOfBoundsException("Out of bounds: " + p);
        }
        tiles[p.y()][p.x()] = tile;
    }

    public boolean isWalkable(Position p) {
        return get(p).isWalkable();
    }
}
