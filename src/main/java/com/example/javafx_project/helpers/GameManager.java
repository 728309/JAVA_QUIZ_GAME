package com.example.javafx_project.helpers;

import com.example.javafx_project.model.Player;
import com.example.javafx_project.model.Quiz;
import com.example.javafx_project.model.Question;

public final class GameManager {
    private static final GameManager INSTANCE = new GameManager();
    private GameManager() {}

    public static GameManager get() { return INSTANCE; }

    private Player player;
    private Quiz quiz;

    // NEW:
    private int index = 0;          // current question index
    private int correct = 0;        // number of correct answers
    private double points = 0.0;

    public void setPlayer(Player p) { this.player = p; }
    public Player getPlayer() { return player; }

    public void setQuiz(Quiz q) { this.quiz = q; this.index = 0; this.correct = 0; this.points = 0.0;}
    public Quiz getQuiz() { return quiz; }

    public boolean readyToStart() { return player != null && quiz != null; }

    public Question currentQuestion() { return quiz.getQuestions().get(index); }
    public int currentIndex() { return index; }
    public int total() { return quiz.getQuestions().size(); }

    public void addCorrect() { correct++; }
    public int getCorrect() { return correct; }
    public void addPoints(double p) { points += Math.max(0, Math.min(1, p)); }  // clamp
    public double getPoints() { return points; }

    public boolean hasNext() { return index + 1 < total(); }
    public void goNext() { if (hasNext()) index++; }

    public void reset() { if (quiz != null) { index = 0; correct = 0; points = 0.0; } }
}
