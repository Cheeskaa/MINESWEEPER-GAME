package minesweeper.main;

import minesweeper.model.AbstractTile;
import minesweeper.model.TileFactory;
import minesweeper.model.TreasureTile;
import minesweeper.model.MineTile;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameBoard {
    private final int difficulty;
    private JPanel boardPanel;
    private JFrame frame;
    private Timer timer;
    private int elapsedTime;
    private JLabel timerLabel;
    private AbstractTile[][] board;
    private int numRows;
    private int numCols;
    private int tileSize;
    private int tilesClicked;
    private List<AbstractTile> mineList;
    private List<AbstractTile> treasureList;
    private boolean gameOver;
    private int highScore;
    private int mineCount;

    public GameBoard(int difficulty) {
        if (difficulty < 0 || difficulty > 2) {
            throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
        }
        this.difficulty = difficulty;
        setGridSize(difficulty);
        loadHighScore(difficulty);
    }

    private void setGridSize(int difficulty) {
        switch (difficulty) {
            case 0: // Easy
                numRows = 8;
                numCols = 8;
                tileSize = 70;
                mineCount = 10;
                break;
            case 1: // Medium
                numRows = 12;
                numCols = 12;
                tileSize = 60;
                mineCount = 20;
                break;
            case 2: // Hard
                numRows = 16;
                numCols = 16;
                tileSize = 48;
                mineCount = 40;
                break;
            default:
                throw new IllegalStateException("Invalid difficulty");
        }
    }

    public void setup() {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(numRows, numCols));

        initializeTiles();

        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 25));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);

        frame.add(timerLabel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.setSize(numCols * tileSize, numRows * tileSize + 50);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        startTimer();
    }

    private void initializeTiles() {
        board = new AbstractTile[numRows][numCols];
        mineList = new ArrayList<>();
        treasureList = new ArrayList<>();

        try {
            List<AbstractTile> tiles = TileFactory.createTiles(difficulty, this);
            if (tiles == null || tiles.isEmpty()) {
                throw new IllegalStateException("TileFactory returned invalid tiles");
            }

            for (AbstractTile tile : tiles) {
                if (tile == null) continue;

                int row = tile.getRow();
                int col = tile.getCol();

                if (row >= 0 && row < numRows && col >= 0 && col < numCols) {
                    board[row][col] = tile;
                    boardPanel.add(tile.getButton());

                    if (tile instanceof MineTile) {
                        mineList.add(tile);
                    } else if (tile instanceof TreasureTile) {
                        treasureList.add(tile);
                    }
                }
            }

            if (mineList.isEmpty()) {
                throw new IllegalStateException("No mines were placed on the board");
            }

        } catch (Exception e) {
            System.err.println("Error initializing tiles: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game board", e);
        }
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            elapsedTime++;
            timerLabel.setText("Time: " + elapsedTime + "s");
        });
        timer.start();
    }

    public void gameOver(String message, boolean won) {
        timer.stop();

        String scoreMessage = "Time: " + elapsedTime + "s\nHigh Score: " + highScore + "s";

        if (won) {
            if (elapsedTime < highScore) {
                highScore = elapsedTime;
                saveHighScore(difficulty);
                scoreMessage = "New High Score: " + elapsedTime + "s";
            }
        }

        showCustomGameOverDialog(message, scoreMessage, won);
        frame.dispose();
    }

    public void showCustomGameOverDialog(String message, String scoreMessage, boolean won) {
        JFrame frame = new JFrame("Game Over");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        if (!isValidCell(r, c) || !board[r][c].getButton().isEnabled() || board[r][c] instanceof MineTile) {
            return;
        }
    
        AbstractTile tile = board[r][c];
        tile.getButton().setEnabled(false);
        tilesClicked++;
    
        int adjacentMines = countAdjacentMines(r, c);
        if (adjacentMines > 0) {
            // Show number with styling
            tile.getButton().setText(String.valueOf(adjacentMines));
            tile.getButton().setFont(new Font("Arial", Font.BOLD, 20));
            tile.getButton().setForeground(getNumberColor(adjacentMines));
            tile.getButton().setBackground(new Color(220, 220, 220));
        } else {
            // Flood fill empty areas
            tile.getButton().setText("");
            tile.getButton().setBackground(new Color(200, 200, 200));
            
            // Recursive flood fill
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    checkMine(r + dr, c + dc);
                }
            }
        }
    
        // Check win condition
        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver("Congratulations! You cleared all mines!", true);
        }
    }
    
    public int countAdjacentMines(int r, int c) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                if (isValidCell(r + dr, c + dc) && board[r + dr][c + dc] instanceof MineTile) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private boolean isValidCell(int r, int c) {
        return r >= 0 && r < numRows && c >= 0 && c < numCols;
    }
    
    private Color getNumberColor(int number) {
        switch (number) {
            case 1: return Color.BLUE;
            case 2: return Color.GREEN;
            case 3: return Color.RED;
            case 4: return Color.MAGENTA;
            case 5: return Color.ORANGE;
            default: return Color.BLACK;
        }
    }

    public void revealMines() {
        if (mineList == null || mineList.isEmpty()) {
            gameOver("Game Over!", false);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            Timer bombAnimationTimer = new Timer(200, null);
            final int[] step = {0};

            bombAnimationTimer.addActionListener(e -> {
                try {
                    for (AbstractTile tile : mineList) {
                        JButton button = tile.getButton();
                        if (button != null) {
                            switch (step[0]) {
                                case 0:
                                    button.setBackground(Color.RED);
                                    button.setText("💣");
                                    break;
                                case 1:
                                    button.setBackground(Color.ORANGE);
                                    break;
                                case 2:
                                    button.setBackground(Color.YELLOW);
                                    break;
                                case 3:
                                    button.setBackground(Color.BLACK);
                                    break;
                            }
                        }
                    }

                    step[0]++;

                    if (step[0] > 3) {
                        bombAnimationTimer.stop();
                        SwingUtilities.invokeLater(this::showEndScreen);
                    }
                } catch (Exception ex) {
                    bombAnimationTimer.stop();
                    gameOver("Game Over!", false);
                }
            });

            bombAnimationTimer.start();
        });
    }

    public void showEndScreen() {
        for (AbstractTile treasure : treasureList) {
            treasure.getButton().setText("💎");
            treasure.getButton().setForeground(Color.decode("#FFD700")); // Gold for treasure
        }
        playBombSound();
        gameOver("Game Over! You hit a bomb.", false);
    }

    public void revealTreasure(AbstractTile tile) {
        tile.getButton().setText("💎");
        tile.getButton().setForeground(Color.decode("#FFD700"));
        tile.getButton().removeMouseListener(tile.getButton().getMouseListeners()[0]);  // Remove click functionality
        gameOver("You found the treasure!", true);
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

    public void loadHighScore(int difficulty) {
        String filename = "highscore_" + getDifficultyName(difficulty) + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            highScore = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }

    public void saveHighScore(int difficulty) {
        String filename = "highscore_" + getDifficultyName(difficulty) + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

}