package minesweeper.model;

import java.awt.Color;
import minesweeper.main.GameBoard;

public class MineTile extends AbstractTile {
    public MineTile(int r, int c, GameBoard game) {
        super(r, c, game);
    }

    @Override
    public void handleClick() {
        button.setText("💣");
        button.setBackground(Color.RED);
        game.revealMines();
    }
}