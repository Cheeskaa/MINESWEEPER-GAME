package minesweeper.model;

import minesweeper.main.GameBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileFactory {
    public static List<AbstractTile> createTiles(int difficulty, GameBoard game) {
        List<AbstractTile> tiles = new ArrayList<>();
        int numRows = game.getNumRows();
        int numCols = game.getNumCols();
        int mineCount = getMineCount(difficulty);
        int treasureCount = 1;

        Random random = new Random();

        // Create mines
        while (mineCount > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            AbstractTile tile = new MineTile(r, c, game);
            if (!tiles.contains(tile)) {
                tiles.add(tile);
                mineCount--;
            }
        }

        // Create treasures
        while (treasureCount > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            AbstractTile tile = new TreasureTile(r, c, game);
            if (!tiles.contains(tile)) {
                tiles.add(tile);
                treasureCount--;
            }
        }

        // Create empty tiles
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                AbstractTile tile = new EmptyTile(r, c, game);
                if (!tiles.contains(tile)) {
                    tiles.add(tile);
                }
            }
        }

        return tiles;
    }

    private static int getMineCount(int difficulty) {
        switch (difficulty) {
            case 0: return 10; // Easy
            case 1: return 20; // Medium
            case 2: return 40; // Hard
            default: return 10;
        }
    }
}