# Dungeon Crawler

A turn-based roguelike for the terminal, written in plain Java 17 with no runtime dependencies. Every level is procedurally generated, and monsters hunt the player using A* pathfinding.

![CI](../../actions/workflows/ci.yml/badge.svg)

```
########....#####.....######.....#######
########.g..#####.....######.....#######
########..........g..........g...#######
#######.......###.....######.....#######
##########......................O>....##
#######........##@..............g.....##
```

`@` you, `g` goblin, `O` orc, `>` stairs. Reach the stairs to win; fight anything in your way.

## Run it

```bash
mvn -q package
java -jar target/dungeon-crawler-1.0.0.jar          # random dungeon
java -jar target/dungeon-crawler-1.0.0.jar 42       # reproducible dungeon (seed 42)
```

Controls: `w` `a` `s` `d` then Enter to move or attack, `q` to quit.

## What's inside

| Piece | What it does |
|---|---|
| `MapGenerator` | Places non-overlapping rooms and joins them with L-shaped corridors. Seeded, so a seed always yields the same dungeon. |
| `Pathfinder` | A* search on a 4-connected grid with a Manhattan-distance heuristic and a pluggable "blocked" predicate. |
| `Entity` / `Player` / `Monster` | Small class hierarchy. Monsters chase with A* once the player is inside their sight radius. |
| `Game` | Turn loop: the player acts, then each monster acts. UI-independent, so it is easy to test. |
| `Main` | Thin console front-end. |

## Design notes

- **Separation of concerns:** `Game` knows nothing about the console. A graphical or networked front-end could reuse it unchanged.
- **Deterministic generation:** all randomness flows from an explicit seed, which makes bugs reproducible and tests reliable.
- **A\* complexity:** O(E log V) with a binary-heap open set. Monsters path around each other by passing a `blocked` predicate.
- **Connectivity guarantee:** every room is joined to the previous one, so the whole dungeon is connected. A test verifies this across 25 seeds.

## Tests

```bash
mvn test
```

16 JUnit 5 tests cover pathfinding (shortest path, walls, unreachable goals, blocked tiles), generation (determinism, no overlaps, connectivity, stairs) and gameplay (movement, combat, chasing, winning and losing).

## Ideas to extend

- Multiple floors and a score, items and potions, field-of-view / fog of war, a Swing or JavaFX front-end.
