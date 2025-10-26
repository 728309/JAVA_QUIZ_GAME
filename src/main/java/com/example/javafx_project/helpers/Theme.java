package com.example.javafx_project.helpers;

import javafx.scene.Scene;

public final class Theme {
    private Theme() {}

    public static void setAccent(Scene scene, String hex) {
        if (scene == null || scene.getRoot() == null) return;

        String existing = scene.getRoot().getStyle();
        String rule = "-color-accent: " + hex + ";";

        if (existing == null || existing.isBlank()) {
            scene.getRoot().setStyle(rule);
            return;
        }

        // replace existing -color-accent if present, else append
        String replaced = existing.replaceAll("(?i)-color-accent\\s*:\\s*#[0-9a-f]{3,8}\\s*;", rule);
        if (replaced.equals(existing)) {
            scene.getRoot().setStyle(existing.trim() + (existing.trim().endsWith(";") ? "" : ";") + rule);
        } else {
            scene.getRoot().setStyle(replaced);
        }
    }
}
