package com.example.javafx_project.service;

import com.example.javafx_project.model.CombiQuestion;
import com.example.javafx_project.model.Question;
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
            JSONArray pages = root.optJSONArray("pages");
            if (pages == null || pages.length() == 0) {
                throw new IllegalArgumentException("Quiz JSON must contain a non-empty 'pages' array.");
            }

            for (int i = 0; i < pages.length(); i++) {
                JSONObject page = pages.getJSONObject(i);
                int timeLimit = page.optInt("timeLimit", 0);

                JSONArray elements = page.optJSONArray("elements");
                if (elements == null || elements.length() == 0) {
                    // no question on this page -> skip gracefully
                    continue;
                }

                JSONObject el = elements.getJSONObject(0);
                String type   = el.optString("type", "");
                String qTitle = el.optString("title", "(untitled)");

                switch (type) {
                    case "radiogroup" -> {
                        JSONArray arr = el.optJSONArray("choices");
                        if (arr == null || arr.length() == 0) {
                            throw new IllegalArgumentException("Radiogroup question is missing 'choices'.");
                        }
                        // map to List<String>
                        List<String> choices = arr.toList().stream().map(Object::toString).toList();

                        String correct = el.optString("correctAnswer", null);
                        if (correct == null) {
                            throw new IllegalArgumentException("Radiogroup question missing 'correctAnswer'.");
                        }

                        // ✅ use factory
                        qs.add(CombiQuestion.multiple(qTitle, choices, correct, timeLimit));
                    }
                    case "boolean" -> {
                        if (!el.has("correctAnswer")) {
                            throw new IllegalArgumentException("Boolean question missing 'correctAnswer'.");
                        }
                        boolean correct = el.getBoolean("correctAnswer");

                        // ✅ use factory
                        qs.add(CombiQuestion.bool(qTitle, correct, timeLimit));
                    }
                    default -> throw new IllegalArgumentException("Unsupported type: " + type);
                }
            }

            if (qs.isEmpty()) {
                throw new IllegalArgumentException("No valid questions were found in the quiz file.");
            }

            return new Quiz(quizId, title, qs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load quiz: " + e.getMessage(), e);
        }
    }
}
