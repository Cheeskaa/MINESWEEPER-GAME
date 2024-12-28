package minesweeper.model;

import java.awt.Color;
import minesweeper.main.GameBoard;

public class EmptyTile extends AbstractTile {
    public EmptyTile(int r, int c, GameBoard game) {
        super(r, c, game);
    }

    @Override
    public void handleClick() {
        if (!button.isEnabled()) return;
        button.setEnabled(false);
        button.setBackground(Color.LIGHT_GRAY);
        game.checkMine(r, c);  // This triggers flood fill
    }
}