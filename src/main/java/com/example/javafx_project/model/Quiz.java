package com.example.javafx_project.model;

import java.util.List;
import java.util.Objects;

public class Quiz {
    private final String quizId;
    private final String title;
    private final List<Question> questions;

    public Quiz(String quizId, String title, List<Question> questions) {
        this.quizId = Objects.requireNonNull(quizId, "quizId");
        this.title  = Objects.requireNonNull(title, "title");
        this.questions = List.copyOf(Objects.requireNonNull(questions, "questions"));
    }

    public String getQuizId() { return quizId; }
    public String getTitle()  { return title;  }
    public List<Question> getQuestions() { return questions; }
}
