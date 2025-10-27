package com.example.javafx_project.helpers;

import com.example.javafx_project.model.Player;
import com.example.javafx_project.model.Question;
import com.example.javafx_project.model.Quiz;

public final class GameManager {
    private static final GameManager INSTANCE = new GameManager();

    private Quiz quiz;
    private Player player;
    private int index;
    private int correct;
    private double points;

    private boolean practiceMode = false;

    private GameManager() {}
    public static GameManager get() { return INSTANCE; }
    public void setPracticeMode(boolean enabled) { this.practiceMode = enabled; }
    public boolean isPracticeMode() { return practiceMode; }


    public void setQuiz(Quiz q) { quiz = q; resetProgress(); }
    public void setPlayer(Player p) { player = p; }

    public Quiz getQuiz() { return quiz; }
    public Player getPlayer() { return player; }

    public Question currentQuestion() { return quiz.getQuestions().get(index); }
    public int total() { return quiz == null ? 0 : quiz.getQuestions().size(); }
    public int currentIndex() { return index; }
    public boolean hasNext() { return index < total() - 1; }

    public void goNext() { if (hasNext()) index++; }
    public void addCorrect() { correct++; }
    public void addPoints(double p) { points += Math.max(0, Math.min(1, p)); }

    public int getCorrect() { return correct; }
    public double getPoints() { return points; }

    public boolean readyToStart() { return quiz != null && player != null; }


    /** Resets only the quiz progress counters. */
    public void resetProgress() {
        index = 0; correct = 0; points = 0.0;
    }

    // ----- utils

    private void ensureQuiz() {
        if (quiz == null || quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            throw new IllegalStateException("Quiz not loaded. Load a quiz before starting.");
        }
    }


    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
