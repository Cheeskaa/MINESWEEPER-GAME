package minesweeper;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper extends JPanel {
    private class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 70;
    int numRows;
    int numCols;
    int boardWidth;
    int boardHeight;
    int emojisize = 40;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JLabel timerLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    int mineCount;
    int treasureCount = 1;  // Number of treasures
    MineTile[][] board;
    ArrayList<MineTile> mineList;
    ArrayList<MineTile> treasureList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;

    Timer timer;
    int elapsedTime = 0; // in seconds
    int highScore = Integer.MAX_VALUE; // in seconds

    public Minesweeper(int choice) {
        loadHighScore();
        setupGame(choice);
        frame.setVisible(true);
    }

    public void setupGame(int choice) {
        switch (choice) {
            case 0:
                mineCount = 10;
                numCols = 8;
                break;
            case 1:
                mineCount = 20;
                numCols = 12;
                break;
            case 2:
                tileSize = 50;
                mineCount = 40;
                numCols = 16;
                emojisize = 30;
                break;
            default:
                break;
        }
        numRows = numCols;
        boardWidth = numCols * tileSize;
        boardHeight = numRows * tileSize;
        board = new MineTile[numRows][numCols];
        frame.setSize(boardWidth, boardHeight + 50); // Add space for timer
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + mineCount);
        textLabel.setOpaque(true);

        timerLabel.setFont(new Font("Arial", Font.BOLD, 25));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Time: 0s");
        timerLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.NORTH);
        textPanel.add(timerLabel, BorderLayout.SOUTH);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel);
        initializeBoard();

        setMines();
        setTreasures();

        startTimer();
    }

    private void initializeBoard() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                if ((r + c) % 2 == 0) {
                    tile.setBackground(Color.decode("#2A0055"));
                } else {
                    tile.setBackground(Color.decode("#47008E"));
                }

                tile.setBorder(BorderFactory.createLineBorder(Color.decode("#1C0039"), 2));  // White gridlines, 2px thick
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, emojisize));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }

                        MineTile tile = (MineTile) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText().equals("")) {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else if (treasureList.contains(tile)) {
                                    revealTreasure(tile);
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText().equals("") && tile.isEnabled()) {
                                tile.setText("🚩");
                                tile.setForeground(Color.RED);
                            } else if (tile.getText().equals("🚩")) {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);
    }

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
        for (MineTile tile : mineList) {
            tile.setText("💣");
            tile.setForeground(Color.decode("#00FF7F"));  // Bomb color (Orange-Red)
        }
        for (MineTile treasure : treasureList) {
            treasure.setText("💎");
            treasure.setForeground(Color.decode("#FFD700"));
        }
        gameOver("Game Over! You hit a bomb.");
    }

    public void revealTreasure(MineTile tile) {
        tile.setText("💎");
        tile.setForeground(Color.decode("#FFD700"));  // Set diamond color (Gold)
        tile.removeMouseListener(tile.getMouseListeners()[0]);  // Remove click functionality
        gameOver("You found the treasure!");
    }

    public void gameOver(String message) {
        timer.stop();
        if (elapsedTime < highScore) {
            highScore = elapsedTime;
            saveHighScore();
            JOptionPane.showMessageDialog(frame, message + "\nNew High Score: " + elapsedTime + "s");
        } else {
            JOptionPane.showMessageDialog(frame, message + "\nTime: " + elapsedTime + "s\nHigh Score: " + highScore + "s");
        }
        SwingUtilities.invokeLater(() -> Main.createAndShowDifficultyDialog());
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
            gameOver("Congratulations! You cleared all mines!");
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

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timerLabel.setText("Time: " + elapsedTime + "s");
            }
        });
        timer.start();
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
            highScore = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            highScore = Integer.MAX_VALUE;
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}