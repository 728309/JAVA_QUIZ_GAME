package com.example.javafx_project.service;

import com.example.javafx_project.model.Result;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public final class ResultService {
    private ResultService() {}

    private static Path resultsDir() {
        return Paths.get(System.getProperty("user.home"), "quiz-results");
    }

    public static Path fileFor(String quizId) {
        return resultsDir().resolve(quizId + "-results.json");
    }

    public static void append(Result r) {
        try {
            Files.createDirectories(resultsDir());
            Path file = fileFor(r.getQuizId());

            JSONObject root;
            if (Files.exists(file)) {
                root = new JSONObject(Files.readString(file, StandardCharsets.UTF_8));
            } else {
                root = new JSONObject()
                        .put("quizId", r.getQuizId())
                        .put("name", r.getQuizName())
                        .put("results", new JSONArray());
            }

            root.getJSONArray("results").put(new JSONObject()
                    .put("playerName", r.getPlayerName())
                    .put("totalQuestions", r.getTotal())
                    .put("correctQuestions", r.getCorrect())
                    .put("date", r.getDate()));

            Files.writeString(file, root.toString(2), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save results", e);
        }
    }

    public static List<Result> readAll(String quizId) {
        try {
            Path file = fileFor(quizId);
            if (!Files.exists(file)) return List.of();

            String text = Files.readString(file, StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(text);
            JSONArray arr = root.getJSONArray("results");

            List<Result> list = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new Result(
                        quizId,
                        root.optString("name", quizId),
                        o.getString("playerName"),
                        o.getInt("totalQuestions"),
                        o.getInt("correctQuestions"),
                        o.getString("date")
                ));
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read results", e);
        }
    }
}
