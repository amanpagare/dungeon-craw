package dev.games.dungeon;

public enum Tile {
    WALL('#', false),
    FLOOR('.', true),
    STAIRS('>', true);

    private final char symbol;
    private final boolean walkable;

    Tile(char symbol, boolean walkable) {
        this.symbol = symbol;
        this.walkable = walkable;
    }

    public char symbol() {
        return symbol;
    }

    public boolean isWalkable() {
        return walkable;
    }
}
