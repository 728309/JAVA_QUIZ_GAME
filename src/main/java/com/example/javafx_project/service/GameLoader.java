package com.example.javafx_project.service;

import com.example.javafx_project.model.CombiQuestion;
import com.example.javafx_project.model.Question;
import com.example.javafx_project.model.QuestionType;
import com.example.javafx_project.model.Quiz;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class GameLoader {
    private GameLoader() {}

    public static Quiz loadFromFile(File file) {
        try {
            String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(text);

            String quizId = root.optString("quizId", "quiz");
            String title  = root.optString("title", "Untitled Quiz");

            List<Question> qs = new ArrayList<>();
            JSONArray pages = root.getJSONArray("pages");
            for (int i = 0; i < pages.length(); i++) {
                JSONObject page = pages.getJSONObject(i);
                int timeLimit = page.optInt("timeLimit", 0);

                // assume one element per page (as in your JSON)
                JSONObject el = page.getJSONArray("elements").getJSONObject(0);
                String type   = el.getString("type");
                String qTitle = el.getString("title");

                switch (type) {
                    case "radiogroup" -> {
                        List<String> choices = el.getJSONArray("choices")
                                .toList().stream().map(Object::toString).toList();
                        String correct = el.get("correctAnswer").toString();
                        qs.add(CombiQuestion.multiple(qTitle, choices, correct, timeLimit));
                    }
                    case "boolean" -> {
                        boolean correct = el.getBoolean("correctAnswer");
                        qs.add(CombiQuestion.bool(qTitle, correct, timeLimit));
                    }
                    default -> throw new IllegalArgumentException("Unsupported element type: " + type);
                }
            }

            return new Quiz(quizId, title, qs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load quiz: " + e.getMessage(), e);
        }
    }
}
