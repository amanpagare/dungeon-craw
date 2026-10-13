package dev.games.dungeon;

/** Axis-aligned rectangular room. */
public record Room(int x, int y, int width, int height) {

    public Position center() {
        return new Position(x + width / 2, y + height / 2);
    }

    /** True if the rooms overlap or touch, including a one-tile margin. */
    public boolean intersects(Room other) {
        return x - 1 < other.x + other.width
                && x + width + 1 > other.x
                && y - 1 < other.y + other.height
                && y + height + 1 > other.y;
    }
}
