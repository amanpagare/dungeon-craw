package dev.games.dungeon;

import java.util.Scanner;

/** Console front-end. Controls: w/a/s/d to move or attack, q to quit. */
public class Main {

    public static void main(String[] args) {
        long seed = args.length > 0 ? Long.parseLong(args[0]) : System.nanoTime();
        Game game = Game.newGame(seed);
        Scanner in = new Scanner(System.in);

        System.out.println("DUNGEON CRAWLER  (seed " + seed + ")");
        System.out.println("Move/attack: w a s d   Quit: q   Reach '>' to win.\n");

        while (game.status() == Game.Status.PLAYING) {
            System.out.print(game.render());
            System.out.println("HP: " + game.player().health() + "/" + Player.MAX_HEALTH);
            game.log().forEach(System.out::println);
            System.out.print("> ");
            if (!in.hasNextLine()) {
                return;
            }
            String line = in.nextLine().trim().toLowerCase();
            if (line.equals("q")) {
                return;
            }
            Game.Direction dir = switch (line) {
                case "w" -> Game.Direction.UP;
                case "s" -> Game.Direction.DOWN;
                case "a" -> Game.Direction.LEFT;
                case "d" -> Game.Direction.RIGHT;
                default -> null;
            };
            if (dir != null) {
                game.playTurn(dir);
            }
        }
        System.out.print(game.render());
        game.log().forEach(System.out::println);
        System.out.println(game.status() == Game.Status.WON ? "VICTORY" : "GAME OVER");
    }
}
