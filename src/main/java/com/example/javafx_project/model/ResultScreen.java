package com.example.javafx_project.model;

public class ResultScreen {
    private final String playerName;
    private final int correct;
    private final int total;
    private final String date;

    public ResultScreen(String playerName, int correct, int total, String date) {
        this.playerName = playerName;
        this.correct = correct;
        this.total = total;
        this.date = date;
    }
    public String getPlayerName() { return playerName; }
    public int getCorrect() { return correct; }
    public int getTotal() { return total; }
    public String getDate() { return date; }
}
