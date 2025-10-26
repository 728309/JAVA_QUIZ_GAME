package com.example.javafx_project.service;

import com.example.javafx_project.model.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class UserService {
    private UserService() {}

    private static List<JSONObject> USERS; // cache

    private static List<JSONObject> users() {
        if (USERS != null) return USERS;
        try (InputStream in = UserService.class.getResourceAsStream(
                "/com/example/javafx_project/data/users.json")) {
            if (in == null) throw new IllegalStateException("users.json not found");
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            JSONArray arr = new JSONArray(text);
            USERS = arr.toList().stream()
                    .map(o -> new JSONObject((java.util.Map<?, ?>) o))
                    .toList();
            return USERS;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read users.json", e);
        }
    }

    public static Player authenticate(String username, String password) {
        return users().stream()
                .filter(u -> username.equals(u.optString("username"))
                        && password.equals(u.optString("password")))
                .findFirst()
                .map(u -> new Player(u.optString("fullname"), username))
                .orElse(null);
    }
}
