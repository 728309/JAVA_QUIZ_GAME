package com.example.javafx_project.model;

public class Result {
    private final String quizId;
    private final String quizName;
    private final String playerName;
    private final int total;
    private final int correct;
    private final String date;

    public Result(String quizId, String quizName, String playerName, int total, int correct, String date) {
        this.quizId = quizId; this.quizName = quizName; this.playerName = playerName;
        this.total = total; this.correct = correct; this.date = date;
    }
    public String getQuizId() { return quizId; }
    public String getQuizName() { return quizName; }
    public String getPlayerName() { return playerName; }
    public int getTotal() { return total; }
    public int getCorrect() { return correct; }
    public String getDate() { return date; }
}
