package minesweeper.model;

import java.awt.Color;
import java.awt.Font;
import minesweeper.main.GameBoard;

public class EmptyTile extends AbstractTile {
    private static final Color REVEALED_COLOR = new Color(220, 220, 220);
    private static final Font NUMBER_FONT = new Font("Arial", Font.BOLD, 20);

    public EmptyTile(int r, int c, GameBoard game) {
        super(r, c, game);
        button.setFont(NUMBER_FONT);
    }

    @Override
    public void handleClick() {
        if (!button.isEnabled()) {
            return;
        }

        button.setEnabled(false);
        button.setBackground(REVEALED_COLOR);
        
        // Debugging statement
        System.out.println("EmptyTile clicked at (" + r + ", " + c + ")");
        
        // Use GameBoard's checkMine method to handle adjacency and flood fill
        game.checkMine(r, c);
    }
}