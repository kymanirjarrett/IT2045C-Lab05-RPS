import java.util.Random;

/**
 * Random strategy: choose a move uniformly at random.
 */
public class RandomStrategy implements Strategy {

    private final Random rng = new Random();

    @Override
    public String getMove(String playerMove) {
        int n = rng.nextInt(3);
        return switch (n) {
            case 0 -> "R";
            case 1 -> "P";
            default -> "S";
        };
    }
}