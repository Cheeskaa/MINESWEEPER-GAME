package minesweeper.main;

import javax.swing.*;
import java.awt.*;

public class DifficultyDialog {
    public void showDialog() {
        JFrame frame = new JFrame("Choose Difficulty");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 1));

        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");

        easyButton.addActionListener(e -> {
            frame.dispose();
            new MinesweeperGame(0).start();
        });

        mediumButton.addActionListener(e -> {
            frame.dispose();
            new MinesweeperGame(1).start();
        });

        hardButton.addActionListener(e -> {
            frame.dispose();
            new MinesweeperGame(2).start();
        });

        frame.add(easyButton);
        frame.add(mediumButton);
        frame.add(hardButton);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}