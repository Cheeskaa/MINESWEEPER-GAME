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

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
            setPreferredSize(new Dimension(70, 70));  // Set a fixed size for the tile
            setFocusable(true);
            setText("");  // Initially empty
        }
    }

    int tileSize = 70;
    int numRows;
    int numCols;
    int boardWidth;
    int boardHeight;
    int fontsize = 45;

    private JButton[][] buttons;
    private boolean[][] mineLocations;
    private boolean treasureFound = false;
    private int treasureRow, treasureCol;
    int mineCount;
    MineTile[][] board; // = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0; // goal is to click all tiles except the ones containing mines
    boolean gameOver = false;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    public Minesweeper(int choice) {
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
                tileSize = 50; //changed it to 50
                mineCount = 40;
                numCols = 16;
                fontsize = 35;
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

        boardPanel.setLayout(new GridLayout(numRows, numCols)); // 8x8
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                // Alternate colors for a checkered pattern
                if ((r + c) % 2 == 0) {
                    tile.setBackground(Color.decode("#C4E1F6")); // Light blue
                } else {
                    tile.setBackground(Color.decode("#FFF5CD")); // Light cream
                }

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, fontsize));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }

                        int r = tile.r;
                        int c = tile.c;

                        if (mineLocations[r][c]) {
                            tile.setText("💣");
                            tile.setBackground(Color.RED);
                            revealAllMinesAndTreasure(); // Show all mines and treasure
                            gameOver(false); // End the game on bomb click
                        } else if (r == treasureRow && c == treasureCol) {
                            tile.setText("💎");
                            tile.setBackground(Color.CYAN);
                            gameOver(true); // Win the game on treasure click
                        } else {
                            checkMine(r, c); // Reveal safe tiles or other logic
                        }

                        // Left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("🚩");
                                tile.setForeground(Color.RED); // Set the flag color to red
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
        placeTreasure();  // Added this to place the treasure
    }

    public void placeTreasure() {
        Random random = new Random();

        while (true) {
            treasureRow = random.nextInt(numRows);
            treasureCol = random.nextInt(numCols);

            // Ensure the treasure is not on a mine
            if (!mineLocations[treasureRow][treasureCol]) {
                break;
            }
        }
    }

    public void clearBoard() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                board[row][col].setEnabled(false);
            }
        }
    }

    public void revealAllMinesAndTreasure() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (mineLocations[row][col]) {
                    board[row][col].setText("💣");
                    board[row][col].setBackground(Color.RED);
                } else if (row == treasureRow && col == treasureCol) {
                    board[row][col].setText("💎");
                    board[row][col].setBackground(Color.CYAN);
                }
            }
        }
    }

    public void gameOver(boolean isWin) {
        if (isWin) {
            textLabel.setText("You Win!");
        } else {
            textLabel.setText("Game Over");
        }
        gameOver = true;  // Set gameOver flag to true
        disableAllTiles();  // Disable all tiles after game over
    }

    public void disableAllTiles() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                board[r][c].setEnabled(false);  // Disable tiles so no further interaction
            }
        }
    }

    public void setMines() {
        mineList = new ArrayList<MineTile>();
        mineLocations = new boolean[numRows][numCols];

        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLocations[r][c] = true;
                mineLeft -= 1;
            }
        }
    }

    public void revealMines() {
        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over!");
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

        // top 3
        minesFound += countMine(r - 1, c - 1); // top left
        minesFound += countMine(r - 1, c); // top
        minesFound += countMine(r - 1, c + 1); // top right

        // left and right
        minesFound += countMine(r, c - 1); // left
        minesFound += countMine(r, c + 1); // right

        // bottom 3
        minesFound += countMine(r + 1, c - 1); // bottom left
        minesFound += countMine(r + 1, c); // bottom
        minesFound += countMine(r + 1, c + 1); // bottom right

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");

            // top 3
            checkMine(r - 1, c - 1); // top left
            checkMine(r - 1, c); // top

            // left and right
            checkMine(r, c - 1); // left
            checkMine(r, c + 1); // right

            // bottom 3
            checkMine(r + 1, c - 1); // bottom left
            checkMine(r + 1, c); // bottom
            checkMine(r + 1, c + 1); // bottom right
        }

        tile.setBackground(Color.decode("#ECCA9C")); // Set revealed tile color
        if (tilesClicked == (numRows * numCols) - mineCount) {
            gameOver(true); // Win if all non-mine tiles are clicked
        }
    }

    public int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }

        if (mineLocations[r][c]) {
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        String[] options = {"Beginner", "Intermediate", "Expert"};
        int choice = JOptionPane.showOptionDialog(null, "Choose Difficulty", "Minesweeper",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        new Minesweeper(choice);
    }
}
