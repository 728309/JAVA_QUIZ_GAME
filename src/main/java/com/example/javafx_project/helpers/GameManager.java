package com.example.javafx_project.helpers;

import com.example.javafx_project.model.Player;
import com.example.javafx_project.model.Quiz;

public final class GameManager {
    private static final GameManager INSTANCE = new GameManager();
    private GameManager() {}

    public static GameManager get() { return INSTANCE; }

    private Player player;
    private Quiz quiz;

    public void setPlayer(Player p) { this.player = p; }
    public Player getPlayer() { return player; }

    public void setQuiz(Quiz q) { this.quiz = q; }
    public Quiz getQuiz() { return quiz; }

    public boolean readyToStart() { return player != null && quiz != null; }
    public void reset() { quiz = null; } // keep player logged in
}
