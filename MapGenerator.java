package dev.games.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Procedural dungeon generator: scatter non-overlapping rooms, then connect
 * each room to the previous one with an L-shaped corridor. Deterministic for a given seed.
 */
public class MapGenerator {

    public record Result(DungeonMap map, List<Room> rooms) {}

    private static final int MIN_ROOM = 4;
    private static final int MAX_ROOM = 9;

    private final int width;
    private final int height;
    private final int roomAttempts;

    public MapGenerator(int width, int height, int roomAttempts) {
        this.width = width;
        this.height = height;
        this.roomAttempts = roomAttempts;
    }

    public Result generate(long seed) {
        Random rng = new Random(seed);
        DungeonMap map = new DungeonMap(width, height);
        List<Room> rooms = new ArrayList<>();

        for (int i = 0; i < roomAttempts; i++) {
            int w = MIN_ROOM + rng.nextInt(MAX_ROOM - MIN_ROOM + 1);
            int h = MIN_ROOM + rng.nextInt(MAX_ROOM - MIN_ROOM + 1);
            if (width - w - 2 <= 1 || height - h - 2 <= 1) {
                continue;
            }
            int x = 1 + rng.nextInt(width - w - 2);
            int y = 1 + rng.nextInt(height - h - 2);
            Room candidate = new Room(x, y, w, h);

            boolean overlaps = rooms.stream().anyMatch(candidate::intersects);
            if (overlaps) {
                continue;
            }
            carveRoom(map, candidate);
            if (!rooms.isEmpty()) {
                carveCorridor(map, rooms.get(rooms.size() - 1).center(), candidate.center(), rng);
            }
            rooms.add(candidate);
        }

        if (rooms.isEmpty()) {
            throw new IllegalStateException("Could not place any rooms; map too small");
        }
        map.set(rooms.get(rooms.size() - 1).center(), Tile.STAIRS);
        return new Result(map, rooms);
    }

    private void carveRoom(DungeonMap map, Room room) {
        for (int y = room.y(); y < room.y() + room.height(); y++) {
            for (int x = room.x(); x < room.x() + room.width(); x++) {
                map.set(new Position(x, y), Tile.FLOOR);
            }
        }
    }

    private void carveCorridor(DungeonMap map, Position a, Position b, Random rng) {
        boolean horizontalFirst = rng.nextBoolean();
        Position corner = horizontalFirst ? new Position(b.x(), a.y()) : new Position(a.x(), b.y());
        carveLine(map, a, corner);
        carveLine(map, corner, b);
    }

    private void carveLine(DungeonMap map, Position from, Position to) {
        int dx = Integer.compare(to.x(), from.x());
        int dy = Integer.compare(to.y(), from.y());
        Position p = from;
        while (true) {
            if (map.get(p) == Tile.WALL) {
                map.set(p, Tile.FLOOR);
            }
            if (p.equals(to)) {
                break;
            }
            p = p.plus(dx, dy);
        }
    }
}
