package com.example.javafx_project.model;

public interface Question {
    String getTitle();
    boolean isCorrect(Object answer);
}
