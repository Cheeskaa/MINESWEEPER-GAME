/**
 * This Java file is for a Minesweeper game. The game is played in a window with a grid of buttons.
 * The player can left-click on a button to reveal what's underneath, and right-click to flag a
 * button. The game ends when all non-mine buttons have been revealed or a mine has been clicked.
 * The number of mines is set by the user when they start the game. The game window is resizable.
 */

package minesweeper;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper extends JPanel {
    private class MineTile extends JButton {
        int r;
        int c;
        boolean hasTreasure = false;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 80;
    int numRows;
    int numCols;
    int boardWidth = numCols * tileSize;
    int boardHeight = numRows * tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    int mineCount;
    MineTile[][] board;
    ArrayList<MineTile> mineList;
    Random random = new Random();
    boolean treasureFound = false;

    int tilesClicked = 0;
    boolean gameOver = false;

    public Minesweeper(int choice) {
        switch (choice) {
            case 0:
                mineCount = 10;
                numCols = 8;
                tileSize = 70;
                break;
            case 1:
                mineCount = 20;
                numCols = 12;
                tileSize = 70;
                break;
            case 2:
                tileSize = 50;
                mineCount = 40;
                numCols = 16;
                break;
            default:
                break;
        }
        numRows = numCols;
        boardWidth = numCols * tileSize;
        boardHeight = numRows * tileSize;
        board = new MineTile[numRows][numCols];

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                if ((r + c) % 2 == 0) {
                    tile.setBackground(Color.decode("#C4E1F6"));
                } else {
                    tile.setBackground(Color.decode("#FFF5CD"));
                }

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver || treasureFound) {
                            return;
                        }

                        MineTile tile = (MineTile) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else if (tile.hasTreasure) {
                                    revealTreasure(tile);
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("🚩");
                                tile.setForeground(Color.RED);
                            } else if (tile.getText() == "🚩") {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);
        setMines();
        setTreasure();
    }

    public void setMines() {
        mineList = new ArrayList<MineTile>();
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

    public void setTreasure() {
        int r = random.nextInt(numRows);
        int c = random.nextInt(numCols);

        MineTile tile = board[r][c];
        if (!mineList.contains(tile)) {
            tile.hasTreasure = true;
        } else {
            setTreasure();
        }
    }

    public void revealMines() {
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over!");
    }

    public void revealTreasure(MineTile tile) {
        tile.setText("💎");
        tile.setForeground(Color.BLUE);
        textLabel.setText("You found the treasure!");
        treasureFound = true;
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
            checkMine(r, c - 1);
            checkMine(r, c + 1);
            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        tile.setBackground(Color.decode("#ECCA9C"));

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
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
}


