/**
 * Cheat strategy: always picks the winning move against the player's move (used no more than 10% of the time.)
 */
public class Cheat implements Strategy {

    @Override
    public String getMove(String playerMove) {
        return switch (playerMove) {
            case "R" -> "P"; // Paper beats Rock
            case "P" -> "S"; // Scissors beats Paper
            case "S" -> "R"; // Rock beats Scissors
            default -> "R";
        };
    }
}