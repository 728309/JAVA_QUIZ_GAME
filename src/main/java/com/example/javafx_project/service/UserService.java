package com.example.javafx_project.service;

import com.example.javafx_project.model.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class UserService {
    private UserService() {}

    public static Player authenticate(String username, String password) {
        try (InputStream in = UserService.class.getResourceAsStream(
                "/com/example/javafx_project/data/users.json")) {

            if (in == null) throw new IllegalStateException("users.json not found");
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            JSONArray arr = new JSONArray(text);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject u = arr.getJSONObject(i);
                if (u.getString("username").equals(username)
                        && u.getString("password").equals(password)) {
                    return new Player(u.getString("fullname"), username);
                }
            }
            return null; // not found
        } catch (Exception e) {
            throw new RuntimeException("Failed to read users.json", e);
        }
    }
}
