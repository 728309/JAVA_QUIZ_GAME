package com.example.javafx_project.model;
import java.util.List;

public class FullQuestion  implements  Question {
    private final String title;
    private final List<String> choices;
    private final String correctAnswer;
    private final int timeLimit;

    public FullQuestion(String title, List<String> choices, String correctAnswer, int  timeLimit) {
        this.title = title;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
        this.timeLimit = timeLimit;
    }
    public String getTitle() { return title; }
    public List<String> getChoices() { return choices; }
    public boolean isCorrect(Object a) { return correctAnswer.equals(a); }
    public int getTimeLimit() { return timeLimit; }
}

