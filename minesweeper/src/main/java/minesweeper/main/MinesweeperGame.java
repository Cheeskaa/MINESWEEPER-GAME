package minesweeper.main;

public class MinesweeperGame {
    private final int difficulty;

    public MinesweeperGame(int difficulty) {
        this.difficulty = difficulty;
    }

    public void start() {
        GameBoard board = new GameBoard(difficulty);
        board.setup();
    }
}