package dev.games.dungeon;

/** A monster that chases the player with A* once the player is within its sight radius. */
public class Monster extends Entity {
    private final int sightRadius;

    public Monster(String name, char symbol, int health, int attack, int sightRadius, Position position) {
        super(name, symbol, health, attack, position);
        this.sightRadius = sightRadius;
    }

    public static Monster goblin(Position position) {
        return new Monster("Goblin", 'g', 8, 2, 8, position);
    }

    public static Monster orc(Position position) {
        return new Monster("Orc", 'O', 16, 4, 6, position);
    }

    public boolean canSee(Position target) {
        return position().manhattanDistance(target) <= sightRadius;
    }

    /** The next step toward the target, or the current position if there is no route. */
    public Position nextStep(DungeonMap map, Position target, java.util.function.Predicate<Position> blocked) {
        var path = Pathfinder.findPath(map, position(), target, blocked);
        if (path.size() < 2) {
            return position();
        }
        return path.get(1);
    }
}
