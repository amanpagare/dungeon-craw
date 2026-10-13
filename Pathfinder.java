package dev.games.dungeon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;

/** A* pathfinding on a 4-connected grid with a Manhattan-distance heuristic. */
public final class Pathfinder {

    private Pathfinder() {}

    private record Node(Position pos, int g, int f) {}

    /**
     * Finds a shortest path from start to goal (inclusive of both ends).
     *
     * @param blocked extra positions that cannot be entered (e.g. other monsters); the goal is always allowed
     * @return the path, or an empty list if the goal is unreachable
     */
    public static List<Position> findPath(DungeonMap map, Position start, Position goal, Predicate<Position> blocked) {
        if (!map.isWalkable(start) || !map.isWalkable(goal)) {
            return List.of();
        }
        PriorityQueue<Node> open = new PriorityQueue<>((a, b) -> Integer.compare(a.f(), b.f()));
        Map<Position, Position> cameFrom = new HashMap<>();
        Map<Position, Integer> bestG = new HashMap<>();
        Set<Position> closed = new HashSet<>();

        bestG.put(start, 0);
        open.add(new Node(start, 0, start.manhattanDistance(goal)));

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.pos().equals(goal)) {
                return reconstruct(cameFrom, goal);
            }
            if (!closed.add(current.pos())) {
                continue;
            }
            for (Position next : current.pos().neighbours()) {
                if (!map.isWalkable(next) || closed.contains(next)) {
                    continue;
                }
                if (!next.equals(goal) && blocked.test(next)) {
                    continue;
                }
                int tentative = current.g() + 1;
                if (tentative < bestG.getOrDefault(next, Integer.MAX_VALUE)) {
                    bestG.put(next, tentative);
                    cameFrom.put(next, current.pos());
                    open.add(new Node(next, tentative, tentative + next.manhattanDistance(goal)));
                }
            }
        }
        return List.of();
    }

    public static List<Position> findPath(DungeonMap map, Position start, Position goal) {
        return findPath(map, start, goal, p -> false);
    }

    private static List<Position> reconstruct(Map<Position, Position> cameFrom, Position goal) {
        List<Position> path = new ArrayList<>();
        Position p = goal;
        path.add(p);
        while (cameFrom.containsKey(p)) {
            p = cameFrom.get(p);
            path.add(p);
        }
        Collections.reverse(path);
        return path;
    }
}
