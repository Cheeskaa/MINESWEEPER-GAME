package minesweeper.model;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import minesweeper.main.GameBoard;

public abstract class AbstractTile {
    protected int r;
    protected int c;
    protected JButton button;
    protected GameBoard game;

    public AbstractTile(int r, int c, GameBoard game) {
        this.r = r;
        this.c = c;
        this.game = game;
        this.button = new JButton();
        this.button.addActionListener(e -> handleClick());
        styleButton();
    }

    public JButton getButton() {
        return button;
    }

    public int getRow() {
        return r;
    }

    public int getCol() {
        return c;
    }

    public abstract void handleClick();

    private void styleButton() {
        button.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(Color.decode("#1C0039"), 2));
        if ((r + c) % 2 == 0) {
            button.setBackground(Color.decode("#2A0055"));
        } else {
            button.setBackground(Color.decode("#47008E"));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AbstractTile that = (AbstractTile) obj;
        return r == that.r && c == that.c;
    }

    @Override
    public int hashCode() {
        return 31 * r + c;
    }
}