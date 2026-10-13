package dev.games.dungeon;

public class Player extends Entity {
    public static final int MAX_HEALTH = 30;

    public Player(Position position) {
        super("Hero", '@', MAX_HEALTH, 5, position);
    }
}
