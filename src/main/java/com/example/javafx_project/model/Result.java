package com.example.javafx_project.model;

public class Result {
    private final String quizId;
    private final String quizName;
    private final String playerName;
    private final int total;
    private final int correct;
    private final double points;   // <-- NEW
    private final String date;

    public Result(String quizId, String quizName, String playerName,
                  int total, int correct, double points, String date) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.playerName = playerName;
        this.total = total;
        this.correct = correct;
        this.points = points;     // <-- NEW
        this.date = date;
    }

    public String getQuizId()     { return quizId; }
    public String getQuizName()   { return quizName; }
    public String getPlayerName() { return playerName; }
    public int getTotal()         { return total; }
    public int getCorrect()       { return correct; }
    public double getPoints()     { return points; }   // <-- NEW
    public String getDate()       { return date; }
}
