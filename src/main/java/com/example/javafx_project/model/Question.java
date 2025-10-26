package com.example.javafx_project.model;

import java.util.List;

public interface Question {
    String getTitle();
    boolean isCorrect(Object answer);
    int getTimeLimit();

    // lets UI know how to render/check
    QuestionType getType();

    // for MULTIPLE, real options; for BOOLEAN, return List.of("True","False")
    List<String> getChoices();
}
