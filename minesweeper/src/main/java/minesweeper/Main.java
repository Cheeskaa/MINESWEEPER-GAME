

styleButton(easyButton, Color.GREEN);
styleButton(mediumButton, Color.ORANGE);
styleButton(hardButton, Color.RED);

buttonPanel.add(easyButton);
buttonPanel.add(mediumButton);
buttonPanel.add(hardButton);

panel.add(buttonPanel, BorderLayout.CENTER);

// Create and configure a dialog
JDialog dialog = new JDialog(frame, "Difficulty", true);
dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
dialog.setSize(400, 200);
dialog.setLayout(new BorderLayout());
dialog.add(panel, BorderLayout.CENTER);
dialog.setLocationRelativeTo(null);

// Add action listeners
easyButton.addActionListener(e -> startGame(dialog, 0));
mediumButton.addActionListener(e -> startGame(dialog, 1));
hardButton.addActionListener(e -> startGame(dialog, 2));

dialog.setVisible(true);
frame.dispose();
}

public static void startGame(JDialog dialog, int difficulty) {
dialog.dispose();
// try {
//     Minesweeper minesweeper = new Minesweeper(difficulty);
// } catch (Exception e) {
//     JOptionPane.showMessageDialog(null, "Failed to start Minesweeper: " + e.getMessage(), 
//                                   "Error", JOptionPane.ERROR_MESSAGE);
// }
new Minesweeper(difficulty);
}

// Helper method to style buttons
public static void styleButton(JButton button, Color backgroundColor) {
button.setFont(new Font("Arial", Font.BOLD, 14));
button.setBackground(backgroundColor);
button.setForeground(Color.WHITE);
button.setFocusPainted(false);
button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
}
}