package minesweeper.main;

import minesweeper.model.AbstractTile;
import minesweeper.model.TileFactory;
import minesweeper.model.TreasureTile;
import minesweeper.model.MineTile;
import minesweeper.util.TimerUtility;

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

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setup() {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardPanel = new JPanel(new GridLayout(numRows, numCols));
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
        List<AbstractTile> tiles = TileFactory.createTiles(difficulty, this);
        mineList = new ArrayList<>();
        treasureList = new ArrayList<>();
        for (AbstractTile tile : tiles) {
            board[tile.getRow()][tile.getCol()] = tile;
            boardPanel.add(tile.getButton());
            if (tile instanceof MineTile) {
                mineList.add(tile);
            } else if (tile instanceof TreasureTile) {
                treasureList.add(tile);
            }
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

        AbstractTile tile = board[r][c];
        if (!tile.getButton().isEnabled()) {
            return;
        }
        tile.getButton().setEnabled(false);
        tilesClicked++;

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
            tile.getButton().setText(Integer.toString(minesFound));
        } else {
            tile.getButton().setText("");

            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);
            checkMine(r, c - 1);
            checkMine(r, c + 1);
            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        tile.getButton().setBackground(Color.decode("#1C0039"));

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver("Congratulations! You cleared all mines!", true);
        }
    }

    private int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (board[r][c] instanceof MineTile) {
            return 1;
        }
        return 0;
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
}