package minesweeper.model;

import minesweeper.main.GameBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileFactory {
    public static List<AbstractTile> createTiles(int difficulty, GameBoard game) {
        int rows = game.getNumRows();
        int cols = game.getNumCols();
        List<AbstractTile> tiles = new ArrayList<>();
        Random random = new Random();

        // Create all empty tiles first
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles.add(new EmptyTile(r, c, game));
            }
        }

        // Add mines randomly
        int mineCount = getMineCount(difficulty);
        for (int i = 0; i < mineCount; i++) {
            int index;
            do {
                index = random.nextInt(tiles.size());
            } while (!(tiles.get(index) instanceof EmptyTile));
            
            int r = tiles.get(index).getRow();
            int c = tiles.get(index).getCol();
            tiles.set(index, new MineTile(r, c, game));
        }

        // Add treasure randomly
        int treasureCount = getTreasureCount(difficulty);
        for (int i = 0; i < treasureCount; i++) {
            int index;
            do {
                index = random.nextInt(tiles.size());
            } while (!(tiles.get(index) instanceof EmptyTile));
            
            int r = tiles.get(index).getRow();
            int c = tiles.get(index).getCol();
            tiles.set(index, new TreasureTile(r, c, game));
        }

        return tiles;
    }

    private static int getMineCount(int difficulty) {
        switch (difficulty) {
            case 0: return 10;  // Easy
            case 1: return 20;  // Medium
            case 2: return 40;  // Hard
            default: return 10;
        }
    }

    private static int getTreasureCount(int difficulty) {
        switch (difficulty) {
            case 0: return 1;  // Easy
            case 1: return 2;  // Medium
            case 2: return 3;  // Hard
            default: return 1;
        }
    }
}