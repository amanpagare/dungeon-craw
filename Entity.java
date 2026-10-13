package dev.games.dungeon;

/** Base class for anything that lives on the map and can fight. */
public abstract class Entity {
    private final String name;
    private final char symbol;
    private final int attack;
    private int health;
    private Position position;

    protected Entity(String name, char symbol, int health, int attack, Position position) {
        this.name = name;
        this.symbol = symbol;
        this.health = health;
        this.attack = attack;
        this.position = position;
    }

    public String name() {
        return name;
    }

    public char symbol() {
        return symbol;
    }

    public int health() {
        return health;
    }

    public int attack() {
        return attack;
    }

    public Position position() {
        return position;
    }

    public void moveTo(Position newPosition) {
        this.position = newPosition;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }

    public void heal(int amount, int max) {
        health = Math.min(max, health + amount);
    }

    /** Attack another entity; returns the damage dealt. */
    public int strike(Entity target) {
        target.takeDamage(attack);
        return attack;
    }
}
