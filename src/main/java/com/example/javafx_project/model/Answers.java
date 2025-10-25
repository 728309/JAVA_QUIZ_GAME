package com.example.javafx_project.model;

import java.io.Serializable;

public class Answers implements Question {

    private final String title;
    private final boolean correctAnswer;
    private final int timeLimit;

    public Answers(String title, boolean correctAnswer, int timeLimit) {
        this.title = title;
        this.correctAnswer = correctAnswer;
        this.timeLimit = timeLimit;
    }
    public String getTitle() { return title; }
    public boolean isCorrect(Object a) { return a instanceof Boolean b && b == correctAnswer; }
    public int getTimeLimit() { return timeLimit; }
}
