package dev.games.dungeon;

import java.util.List;

/** Immutable grid coordinate. */
public record Position(int x, int y) {

    public Position plus(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    /** The four orthogonal neighbours (up, down, left, right). */
    public List<Position> neighbours() {
        return List.of(plus(0, -1), plus(0, 1), plus(-1, 0), plus(1, 0));
    }

    public int manhattanDistance(Position other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
}
