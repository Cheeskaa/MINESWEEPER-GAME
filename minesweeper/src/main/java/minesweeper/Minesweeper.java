import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import path.to.Tile;

public class Minesweeper extends JPanel implements GameInterface {

    

    private class MineTile extends Tile {
        public MineTile(int r, int c) {
            super(r, c);
        }
    }

    private int tileSize = 70;
    private int numRows;
    private int numCols;
    private int boardWidth;
    private int boardHeight;
    private int emojisize = 40;
    private JFrame frame = new JFrame("Minesweeper");
    private JLabel textLabel = new JLabel();
    private JLabel timerLabel = new JLabel();
    private JPanel textPanel = new JPanel();
    private JPanel boardPanel = new JPanel();
    private int mineCount;
    private int treasureCount = 1;
    private Tile[][] board;
    private ArrayList<Tile> mineList;
    private ArrayList<Tile> treasureList;
    private Random random = new Random();
    private int tilesClicked = 0;
    private boolean gameOver = false;
    private Timer timer;
    private int elapsedTime = 0;
    private int highScore = Integer.MAX_VALUE;

    // Constructor and other methods

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public Minesweeper(int choice) {
        loadHighScore(choice);
        setupGame(choice);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowDifficultyDialog());
    }

    public static void createAndShowDifficultyDialog() {
        JFrame frame = new JFrame("Difficulty");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a custom panel for the dialog
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.decode("#27214f"));

        // Add a title label
        JLabel label = new JLabel("Choose a Difficulty", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.pink);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(label, BorderLayout.NORTH);

        // Create a button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(Color.decode("#27214f"));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create buttons
        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");

        styleButton(easyButton, Color.GREEN);
        styleButton(mediumButton, Color.ORANGE);
        styleButton(hardButton, Color.RED);

        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        // Create and configure a dialog
        JDialog dialog = new JDialog(frame, "Difficulty", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);

        // Add action listeners
        easyButton.addActionListener(e -> startGame(dialog, 0));
        mediumButton.addActionListener(e -> startGame(dialog, 1));
        hardButton.addActionListener(e -> startGame(dialog, 2));

        dialog.setVisible(true);
        frame.dispose();
    }

    public static void startGame(JDialog dialog, int difficulty) {
        dialog.dispose();
        new Minesweeper(difficulty);
    }

    public static void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    @Override
    public void setupGame(int choice) {
        switch (choice) {
            case 0: mineCount = 10; numCols = 8; break;
            case 1: tileSize = 60; emojisize = 40; mineCount = 20; numCols = 12; break;
            case 2: tileSize = 48; mineCount = 40; numCols = 16; emojisize = 30; break;
            default: break;
        }
        numRows = numCols;
        boardWidth = numCols * tileSize;
        boardHeight = numRows * tileSize;
        board = new Tile[numRows][numCols];
        frame.setSize(getBoardWidth(), getBoardHeight() + 50);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
    
        // Title area styling
        setupLabels();
        setupBoardPanel();
        initializeBoard();
    
        setMines();
        setTreasures();
    
        startTimer();
    }

    private void setupLabels() {
        textLabel.setFont(new Font("Arial", Font.BOLD, 30));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + mineCount);
        textLabel.setOpaque(true);
        textLabel.setBackground(Color.decode("#8B0000"));
        textLabel.setForeground(Color.WHITE);
        textLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        
        timerLabel.setFont(new Font("Arial", Font.BOLD, 25));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Time: 0s");
        timerLabel.setOpaque(true);
    
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.NORTH);
        textPanel.add(timerLabel, BorderLayout.SOUTH);
        frame.add(textPanel, BorderLayout.NORTH);
    }

    private void setupBoardPanel() {
        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel);
    }

    @Override
    public void initializeBoard() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Tile tile = new MineTile(r, c);
                board[r][c] = tile;
                setTileBackground(tile, r, c);
                setupTileActions(tile);
                boardPanel.add(tile);
            }
        }
        frame.setVisible(true);
    }

    private void setTileBackground(Tile tile, int r, int c) {
        if ((r + c) % 2 == 0) {
            tile.setBackground(Color.decode("#2A0055"));
        } else {
            tile.setBackground(Color.decode("#47008E"));
        }
        tile.setBorder(BorderFactory.createLineBorder(Color.decode("#1C0039"), 2));
        tile.setFocusable(false);
        tile.setMargin(new Insets(0, 0, 0, 0));
        tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, emojisize));
    }

    private void setupTileActions(Tile tile) {
        tile.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameOver) return;

                if (e.getButton() == MouseEvent.BUTTON1) {
                    handleLeftClick(tile);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    handleRightClick(tile);
                }
            }
        });
    }

    private void handleLeftClick(Tile tile) {
        if (tile.getText().equals("")) {
            if (mineList.contains(tile)) {
                revealMines();
            } else if (treasureList.contains(tile)) {
                revealTreasure(tile);
            } else {
                checkMine(tile.getR(), tile.getC());
            }
        }
    }

    @Override
    public void setMines() {
        mineList = new ArrayList<>();
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    @Override
    public void setTreasures() {
        treasureList = new ArrayList<>();
        int treasureLeft = treasureCount;
        while (treasureLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile) && !treasureList.contains(tile)) {
                treasureList.add(tile);
                treasureLeft -= 1;
            }
        }
    }

    public void revealMines() {
        Timer bombAnimationTimer = new Timer(100, new ActionListener() {
            int step = 0; // Step of the animation
           
    
            @Override
            public void actionPerformed(ActionEvent e) {
                for (MineTile tile : mineList) {
                    if (step == 0) {
                        tile.setBackground(Color.RED); // First frame: Red background
                        tile.setText("💣");
                    } else if (step == 1) {
                        tile.setBackground(Color.ORANGE); // Second frame: Orange background
                    } else if (step == 2) {
                        tile.setBackground(Color.YELLOW); // Third frame: Yellow background
                    } else if (step == 3) {
                        tile.setBackground(Color.BLACK); // Final frame: Black background
                    }
                }
    
                step++;
    
                if (step > 3) {
                    ((Timer) e.getSource()).stop();
                    showEndScreen();
                }
            }
        });
    
        bombAnimationTimer.start();
    }

    public void showEndScreen() {
        for (MineTile treasure : treasureList) {
            treasure.setText("💎");
            treasure.setForeground(Color.decode("#FFD700")); // Gold for treasure
        }
        playBombSound();
        gameOver("Game Over! You hit a bomb.", false);
        
    }

    public void revealTreasure(MineTile tile) {
        tile.setText("💎");
        tile.setForeground(Color.decode("#FFD700"));
        tile.removeMouseListener(tile.getMouseListeners()[0]);  // Remove click functionality
        gameOver("You found the treasure!", true);
    }

    @Override
    public void gameOver(String message, boolean win) {
        timer.stop();

        String scoreMessage = "Time: " + elapsedTime + "s\nHigh Score: " + highScore + "s";

        if (win) {
            if (elapsedTime < highScore) {
                highScore = elapsedTime;
                saveHighScore(numCols == 8 ? 0 : numCols == 12 ? 1 : 2);
                scoreMessage = "New High Score: " + elapsedTime + "s";
            }
        }

        showCustomGameOverDialog(message, scoreMessage, win);
        frame.dispose();
    }

    public void showCustomGameOverDialog(String message, String scoreMessage, boolean win) {
        JFrame frame = new JFrame("Game Over");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Custom panel same as difficulty dialog
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.decode("#27214f"));

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        panel.add(label, BorderLayout.NORTH);

        JLabel scoreLabel = new JLabel("<html>" + scoreMessage.replace("\n", "<br>") + "</html>", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(scoreLabel, BorderLayout.CENTER);

        // Button Panel (Retry and Quit)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBackground(Color.decode("#27214f"));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton retryButton = new JButton("Retry");
        JButton quitButton = new JButton("Quit");

        Main.styleButton(retryButton, Color.GREEN);
        Main.styleButton(quitButton, Color.RED);

        buttonPanel.add(retryButton);
        buttonPanel.add(quitButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(frame, "Game Over", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);

        retryButton.addActionListener(e -> {
            dialog.dispose();
            SwingUtilities.invokeLater(() -> Main.createAndShowDifficultyDialog());
        });

        quitButton.addActionListener(e -> {
            dialog.dispose();
            System.exit(0);
        });

        dialog.setVisible(true);
        frame.dispose();
    }

    public void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }

        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;
        minesFound += countMine(r - 1, c - 1);
        minesFound += countMine(r - 1, c);
        minesFound += countMine(r - 1, c + 1);
        minesFound += countMine(r, c - 1);
        minesFound += countMine(r, c + 1);
        minesFound += countMine(r + 1, c - 1);
        minesFound += countMine(r + 1, c);
        minesFound += countMine(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
            tile.setForeground(Color.WHITE);
        } else {
            tile.setText("");

            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);
            checkMine(r, c - 1);
            checkMine(r, c + 1);
            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        tile.setBackground(Color.decode("#1C0039")); // Set revealed tile color

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver("Congratulations! You cleared all mines!", true);
        }
    }

    public int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }

    @Override
    public void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timerLabel.setText("Time: " + elapsedTime + "s");
            }
        });
        timer.start();
    }

    @Override
    public void loadHighScore(int difficulty) {
        String filename = "highscore_" + getDifficultyName(difficulty) + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            highScore = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }

    @Override
    public void saveHighScore(int difficulty) {
        String filename = "highscore_" + getDifficultyName(difficulty) + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



// Helper method to get difficulty name
    private String getDifficultyName(int difficulty) {
        switch (difficulty) {
            case 0:
                return "easy";
            case 1:
                return "medium";
            case 2:
                return "hard";
            default:
                return "unknown";
        }
    }

    private void playBombSound() {
        try {
            File soundFile = new File("bomb_sound.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
