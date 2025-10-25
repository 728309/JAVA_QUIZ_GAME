package com.example.javafx_project.model;

import java.io.Serializable;

public class Answers implements Question {

    private final String title;
    private final boolean correctAnswer;

    public Answers(String title, boolean correctAnswer) {
        this.title = title;
        this.correctAnswer = correctAnswer;
    }
    public String getTitle() { return title; }
    public boolean isCorrect(Object a) { return a instanceof Boolean b && b == correctAnswer; }
}
