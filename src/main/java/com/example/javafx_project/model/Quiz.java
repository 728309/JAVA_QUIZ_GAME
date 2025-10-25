package com.example.javafx_project.model;

import java.util.List;

public class Quiz {
    private final String quizId;
    private final String title;
    private final java.util.List<Question> questions;

    public Quiz(String quizId, String title, List<Question> questions) {
        this.quizId = quizId; this.title = title; this.questions = questions;
    }
    public String getQuizId() { return quizId; }
    public String getTitle()  { return title;  }
    public List<Question> getQuestions() { return questions; }
}
