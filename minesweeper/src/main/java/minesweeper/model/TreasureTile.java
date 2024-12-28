package minesweeper.model;

import java.awt.Color;
import minesweeper.main.GameBoard;

public class TreasureTile extends AbstractTile {
    public TreasureTile(int r, int c, GameBoard game) {
        super(r, c, game);
    }

    @Override
    public void handleClick() {
        button.setText("💎");
        button.setBackground(Color.YELLOW);
        game.revealTreasure(this);
    }
}