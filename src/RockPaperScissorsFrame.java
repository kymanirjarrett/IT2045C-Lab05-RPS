import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class RockPaperScissorsFrame extends JFrame {

    // Session tracking
    private int playerRockCount = 0;
    private int playerPaperCount = 0;
    private int playerScissorsCount = 0;
    private String lastPlayerMove = null; // for Last Used

    // Stats ---
    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;

    // UI components ---
    private final JTextField playerWinsField = new JTextField("0", 5);
    private final JTextField computerWinsField = new JTextField("0", 5);
    private final JTextField tiesField = new JTextField("0", 5);

    private final JTextArea resultsArea = new JTextArea(12, 40);

    private final JButton rockButton = new JButton("Rock");
    private final JButton paperButton = new JButton("Paper");
    private final JButton scissorsButton = new JButton("Scissors");
    private final JButton quitButton = new JButton("Quit");

    // External Strategies
    private final Strategy randomStrategy = new RandomStrategy();
    private final Strategy cheatStrategy = new Cheat();

    // Inner Strategies
    private final Strategy leastUsedStrategy = new LeastUsedStrategy();
    private final Strategy mostUsedStrategy = new MostUsedStrategy();
    private final Strategy lastUsedStrategy = new LastUsedStrategy();

    private final Random rng = new Random();

    public RockPaperScissorsFrame() {
        setTitle("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createButtonPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createStatsPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        p.setBorder(new TitledBorder("Choose your move"));

        // Load icons from src/images (classpath)
        rockButton.setIcon(loadIcon("/images/rock.png"));
        paperButton.setIcon(loadIcon("/images/paper.png"));
        scissorsButton.setIcon(loadIcon("/images/scissors.png"));
        quitButton.setIcon(loadIcon("/images/quit.png"));

        // Single ActionListener for buttons
        rockButton.addActionListener(this::handleMove);
        paperButton.addActionListener(this::handleMove);
        scissorsButton.addActionListener(this::handleMove);

        quitButton.addActionListener(e -> System.exit(0));

        p.add(rockButton);
        p.add(paperButton);
        p.add(scissorsButton);
        p.add(quitButton);
        return p;
    }

    private JPanel createCenterPanel() {
        JPanel p = new JPanel(new BorderLayout());
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(resultsArea);
        sp.setBorder(new TitledBorder("Game Results (this session)"));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createStatsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 6, 8, 8));
        p.setBorder(new TitledBorder("Stats"));

        // Read-only fields
        playerWinsField.setEditable(false);
        computerWinsField.setEditable(false);
        tiesField.setEditable(false);

        p.add(new JLabel("Player Wins:"));
        p.add(playerWinsField);

        p.add(new JLabel("Computer Wins:"));
        p.add(computerWinsField);

        p.add(new JLabel("Ties:"));
        p.add(tiesField);

        return p;
    }

    private void handleMove(ActionEvent ae) {
        String playerMove = moveFromEvent(ae);

        // Update player move counts + remember last move
        incrementPlayerCounts(playerMove);

        // Pick strategy using 1..100 (inclusive) and required ranges
        int roll = rng.nextInt(100) + 1;

        Strategy chosen;
        String strategyName;

        if (roll <= 10) {
            chosen = cheatStrategy;
            strategyName = "Cheat";
        } else if (roll <= 30) {
            chosen = leastUsedStrategy;
            strategyName = "Least Used";
        } else if (roll <= 50) {
            chosen = mostUsedStrategy;
            strategyName = "Most Used";
        } else if (roll <= 70) {
            chosen = lastUsedStrategy;
            strategyName = "Last Used";
        } else {
            chosen = randomStrategy;
            strategyName = "Random";
        }

        String computerMove = chosen.getMove(playerMove);

        // Determine outcome + build result line
        String line = buildResultLine(playerMove, computerMove, strategyName);

        // Append one line per round
        resultsArea.append(line + "\n");

        // Update stats fields
        playerWinsField.setText(String.valueOf(playerWins));
        computerWinsField.setText(String.valueOf(computerWins));
        tiesField.setText(String.valueOf(ties));
    }

    private String moveFromEvent(ActionEvent ae) {
        Object src = ae.getSource();
        if (src == rockButton) return "R";
        if (src == paperButton) return "P";
        return "S";
    }

    private void incrementPlayerCounts(String playerMove) {
        switch (playerMove) {
            case "R" -> playerRockCount++;
            case "P" -> playerPaperCount++;
            case "S" -> playerScissorsCount++;
        }
        lastPlayerMove = playerMove;
    }

    private String buildResultLine(String playerMove, String computerMove, String strategyName) {
        // Tie
        if (playerMove.equals(computerMove)) {
            ties++;
            return moveWord(playerMove) + " ties " + moveWord(computerMove)
                    + ". (Tie! Computer: " + strategyName + ")";
        }

        // Player wins cases
        if (playerMove.equals("R") && computerMove.equals("S")) {
            playerWins++;
            return "Rock breaks Scissors. (Player wins! Computer: " + strategyName + ")";
        }
        if (playerMove.equals("P") && computerMove.equals("R")) {
            playerWins++;
            return "Paper covers Rock. (Player wins! Computer: " + strategyName + ")";
        }
        if (playerMove.equals("S") && computerMove.equals("P")) {
            playerWins++;
            return "Scissors cuts Paper. (Player wins! Computer: " + strategyName + ")";
        }

        // Otherwise computer wins
        computerWins++;
        if (computerMove.equals("R") && playerMove.equals("S")) {
            return "Rock breaks Scissors. (Computer wins! Computer: " + strategyName + ")";
        }
        if (computerMove.equals("P") && playerMove.equals("R")) {
            return "Paper covers Rock. (Computer wins! Computer: " + strategyName + ")";
        }
        return "Scissors cuts Paper. (Computer wins! Computer: " + strategyName + ")";
    }

    private String moveWord(String move) {
        return switch (move) {
            case "R" -> "Rock";
            case "P" -> "Paper";
            default -> "Scissors";
        };
    }

    private ImageIcon loadIcon(String resourcePath) {
        java.net.URL url = getClass().getResource(resourcePath);
        return (url == null) ? null : new ImageIcon(url);
    }

    // Inner Strategy Classes

    /**
     * Least Used strategy:
     * Identify the player's least-used symbol and play the move that beats it.
     */
    private class LeastUsedStrategy implements Strategy {
        @Override
        public String getMove(String playerMove) {
            // Determine player's least used symbol so far
            int r = playerRockCount;
            int p = playerPaperCount;
            int s = playerScissorsCount;

            String least;
            if (r <= p && r <= s) least = "R";
            else if (p <= r && p <= s) least = "P";
            else least = "S";

            return winningMoveAgainst(least);
        }
    }

    /**
     * Most Used strategy:
     * Identify the player's most-used symbol and play the move that beats it.
     */
    private class MostUsedStrategy implements Strategy {
        @Override
        public String getMove(String playerMove) {
            int r = playerRockCount;
            int p = playerPaperCount;
            int s = playerScissorsCount;

            String most;
            if (r >= p && r >= s) most = "R";
            else if (p >= r && p >= s) most = "P";
            else most = "S";

            return winningMoveAgainst(most);
        }
    }

    /**
     * Last Used strategy:
     * Use the player's previous move. Must handle first round safely.
     */
    private class LastUsedStrategy implements Strategy {
        @Override
        public String getMove(String playerMove) {
            // If called before lastPlayerMove is available, fall back to random
            if (lastPlayerMove == null) {
                return randomStrategy.getMove(playerMove);
            }
            // "Use the symbol the player used on the last round"
            return lastPlayerMove;
        }
    }

    private String winningMoveAgainst(String move) {
        return switch (move) {
            case "R" -> "P";
            case "P" -> "S";
            default -> "R";
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RockPaperScissorsFrame::new);
    }
}