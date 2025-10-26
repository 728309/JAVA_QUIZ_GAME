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

    private static Path dir() { return Paths.get(System.getProperty("user.home"), "quiz-results"); }
    public static Path fileFor(String quizId) { return dir().resolve(quizId + "-results.json"); }

    private static JSONObject loadOrInit(String quizId, String quizName) throws IOException {
        Files.createDirectories(dir());
        Path f = fileFor(quizId);
        if (Files.exists(f)) {
            return new JSONObject(Files.readString(f, StandardCharsets.UTF_8));
        }
        return new JSONObject()
                .put("quizId", quizId)
                .put("name", quizName)
                .put("results", new JSONArray());
    }

    private static void save(String quizId, JSONObject root) throws IOException {
        Files.createDirectories(dir());
        Files.writeString(
                fileFor(quizId),
                root.toString(2),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public static void append(Result r) {
        try {
            JSONObject root = loadOrInit(r.getQuizId(), r.getQuizName());
            root.getJSONArray("results").put(new JSONObject()
                    .put("playerName", r.getPlayerName())
                    .put("totalQuestions", r.getTotal())
                    .put("correctQuestions", r.getCorrect())
                    .put("points", r.getPoints())
                    .put("date", r.getDate()));
            save(r.getQuizId(), root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save results", e);
        }
    }

    public static List<Result> readAll(String quizId) {
        try {
            Path f = fileFor(quizId);
            if (!Files.exists(f)) return List.of();

            JSONObject root = new JSONObject(Files.readString(f, StandardCharsets.UTF_8));
            JSONArray arr = root.getJSONArray("results");
            List<Result> out = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                int correct = o.optInt("correctQuestions", 0);
                out.add(new Result(
                        quizId,
                        root.optString("name", quizId),
                        o.optString("playerName", "Unknown"),
                        o.optInt("totalQuestions", 0),
                        correct,
                        o.has("points") ? o.optDouble("points", correct) : correct, // backward compatible
                        o.optString("date", "")
                ));
            }
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read results", e);
        }
    }
}
