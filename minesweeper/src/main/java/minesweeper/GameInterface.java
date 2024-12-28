import javax.swing.*;
import java.awt.*;

public interface GameInterface {
    public void setupGame(int choice);
    public void initializeBoard();
    public void setMines();
    public void setTreasures();
    public void gameOver(String message, boolean win);
    public void startTimer();
    public void loadHighScore(int difficulty);
    public void saveHighScore(int difficulty);
    
}
