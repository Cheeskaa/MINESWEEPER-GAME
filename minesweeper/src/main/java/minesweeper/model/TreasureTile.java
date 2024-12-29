package minesweeper.model;

import java.awt.Color;
import java.awt.Font;
import minesweeper.main.GameBoard;
import javax.swing.JButton;

public class TreasureTile extends AbstractTile {
    private JButton button;

    public TreasureTile(int r, int c, GameBoard game) {
        super(r, c, game);
        this.button = getButton();
    }

    @Override
    public void handleClick() {
        button.setText("💎");
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20)); // Ensure font supports emoji
        button.setBackground(Color.YELLOW);
        game.revealTreasure(this);
    }
}