package dev.games.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Turn-based game state. The UI layer feeds it {@link Direction}s and renders {@link #render()}. */
public class Game {

    public enum Direction {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public enum Status { PLAYING, WON, LOST }

    private final DungeonMap map;
    private final Player player;
    private final List<Monster> monsters = new ArrayList<>();
    private final List<String> log = new ArrayList<>();
    private Status status = Status.PLAYING;

    /** Creates a game from explicit parts (handy for tests). */
    public Game(DungeonMap map, Player player, List<Monster> monsters) {
        this.map = map;
        this.player = player;
        this.monsters.addAll(monsters);
    }

    /** Generates a fresh random dungeon populated with monsters. */
    public static Game newGame(long seed) {
        MapGenerator.Result result = new MapGenerator(60, 24, 40).generate(seed);
        List<Room> rooms = result.rooms();
        Random rng = new Random(seed ^ 0x9E3779B97F4A7C15L);

        Player player = new Player(rooms.get(0).center());
        List<Monster> monsters = new ArrayList<>();
        for (int i = 1; i < rooms.size(); i++) {
            Position spot = rooms.get(i).center().plus(-1, 0);
            if (!result.map().isWalkable(spot) || result.map().get(spot) == Tile.STAIRS) {
                continue;
            }
            monsters.add(rng.nextInt(3) == 0 ? Monster.orc(spot) : Monster.goblin(spot));
        }
        return new Game(result.map(), player, monsters);
    }

    public Status status() {
        return status;
    }

    public Player player() {
        return player;
    }

    public List<Monster> monsters() {
        return List.copyOf(monsters);
    }

    public List<String> log() {
        return List.copyOf(log);
    }

    /** Plays one full turn: the player acts, then every monster acts. */
    public void playTurn(Direction direction) {
        if (status != Status.PLAYING) {
            return;
        }
        log.clear();
        playerAct(direction);
        if (status == Status.PLAYING) {
            monstersAct();
        }
    }

    private void playerAct(Direction direction) {
        Position target = player.position().plus(direction.dx, direction.dy);
        Monster victim = monsterAt(target);
        if (victim != null) {
            int dmg = player.strike(victim);
            log.add("You hit the " + victim.name() + " for " + dmg + ".");
            if (!victim.isAlive()) {
                log.add("The " + victim.name() + " dies.");
                monsters.remove(victim);
            }
        } else if (map.isWalkable(target)) {
            player.moveTo(target);
            if (map.get(target) == Tile.STAIRS) {
                status = Status.WON;
                log.add("You found the stairs. You win!");
            }
        }
    }

    private void monstersAct() {
        for (Monster m : monsters) {
            if (!m.canSee(player.position())) {
                continue;
            }
            if (m.position().manhattanDistance(player.position()) == 1) {
                int dmg = m.strike(player);
                log.add("The " + m.name() + " hits you for " + dmg + ".");
                if (!player.isAlive()) {
                    status = Status.LOST;
                    log.add("You died.");
                    return;
                }
            } else {
                Position step = m.nextStep(map, player.position(), p -> monsterAt(p) != null);
                if (!step.equals(player.position()) && monsterAt(step) == null) {
                    m.moveTo(step);
                }
            }
        }
    }

    private Monster monsterAt(Position p) {
        for (Monster m : monsters) {
            if (m.position().equals(p)) {
                return m;
            }
        }
        return null;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < map.height(); y++) {
            for (int x = 0; x < map.width(); x++) {
                Position p = new Position(x, y);
                Monster m = monsterAt(p);
                if (p.equals(player.position())) {
                    sb.append(player.symbol());
                } else if (m != null) {
                    sb.append(m.symbol());
                } else {
                    sb.append(map.get(p).symbol());
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
