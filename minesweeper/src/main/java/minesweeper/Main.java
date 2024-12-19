package minesweeper;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Difficulty");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a custom panel for the dialog
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY); // Change the background of the screen

        // Add a title label
        JLabel label = new JLabel("Choose a Difficulty", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.BLACK);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add margin to the label
        panel.add(label, BorderLayout.NORTH);

        // Create a button panel with padding
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(Color.LIGHT_GRAY); // Match the background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add margin around buttons

        // Create buttons
        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");

        styleButton(easyButton, Color.GREEN);
        styleButton(mediumButton, Color.ORANGE);
        styleButton(hardButton, Color.RED);

        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        // Create a dialog
        JDialog dialog = new JDialog(frame, "Difficulty", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);

        // Add action listeners for buttons
        easyButton.addActionListener(e -> {
            dialog.dispose();
            Minesweeper minesweeper = new Minesweeper(0);
        });

        mediumButton.addActionListener(e -> {
            dialog.dispose();
            Minesweeper minesweeper = new Minesweeper(1);
        });

        hardButton.addActionListener(e -> {
            dialog.dispose();
            Minesweeper minesweeper = new Minesweeper(2);
        });

        dialog.setVisible(true);
        frame.dispose();
    }

    // Helper method to style buttons
    private static void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding inside the button
    }
}
