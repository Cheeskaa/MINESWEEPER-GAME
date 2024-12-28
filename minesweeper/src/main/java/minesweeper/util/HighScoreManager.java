package minesweeper.util;

import java.io.*;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscores.txt";

    public static int loadHighScore() {
        int highScore = Integer.MAX_VALUE; // Default to a very high value if no high score is found
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            // Handle exceptions (file not found, number format issues, etc.)
            e.printStackTrace();
        }
        return highScore;
    }

    public static void saveHighScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(Integer.toString(score));
        } catch (IOException e) {
            // Handle exceptions (file write issues, etc.)
            e.printStackTrace();
        }
    }
}