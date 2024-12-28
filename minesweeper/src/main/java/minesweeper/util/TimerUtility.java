package minesweeper.util;

public class TimerUtility {
    private static long startTime;

    public static void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public static long getElapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000; // Time in seconds
    }
}
