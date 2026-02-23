/**
 * Strategy interface for selecting a computer move in Rock-Paper-Scissors.
 */
public interface Strategy {
    /**
     * Returns the computer move as "R", "P", or "S".
     *
     * @param playerMove the player's move ("R", "P", or "S")
     * @return the computer's move ("R", "P", or "S")
     */
    String getMove(String playerMove);
}