package com.example.javafx_project.model;

import java.util.List;

public class Quiz {
    private final String quizId;
    private final String title;
    private final List<Question> questions;
    private final String completedHtml; // NEW (can contain {correctAnswers} and {questionCount})

    public Quiz(String quizId, String title, List<Question> questions) {
        this(quizId, title, questions, "");
    }

    public Quiz(String quizId, String title, List<Question> questions, String completedHtml) {
        this.quizId = quizId;
        this.title = title;
        this.questions = questions;
        this.completedHtml = completedHtml == null ? "" : completedHtml;
    }

    public String getQuizId() { return quizId; }
    public String getTitle()  { return title;  }
    public List<Question> getQuestions() { return questions; }
    public String getCompletedHtml() { return completedHtml; }
}
