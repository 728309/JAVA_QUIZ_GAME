package com.example.javafx_project.service;

import com.example.javafx_project.model.*;
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
                JSONObject el = page.getJSONArray("elements").getJSONObject(0);
                String type = el.getString("type");
                String qTitle = el.getString("title");

                switch (type) {
                    case "radiogroup" -> {
                        var choices = el.getJSONArray("choices").toList().stream().map(Object::toString).toList();
                        String correct = el.getString("correctAnswer");
                        qs.add(new FullQuestion(qTitle, choices, correct, timeLimit));   // <-- pass
                    }
                    case "boolean" -> {
                        boolean correct = el.getBoolean("correctAnswer");
                        qs.add(new Answers(qTitle, correct, timeLimit));          // <-- pass
                    }
                    default -> throw new IllegalArgumentException("Unsupported type: " + type);
                }
            }
            return new Quiz(quizId, title, qs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load quiz: " + e.getMessage(), e);
        }
    }
}
