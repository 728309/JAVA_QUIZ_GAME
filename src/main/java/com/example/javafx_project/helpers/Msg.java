package com.example.javafx_project.helpers;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class Msg {
    private Msg() {}

    public static void info(String msg)  { show(Alert.AlertType.INFORMATION, msg); }
    public static void warn(String msg)  { show(Alert.AlertType.WARNING, msg); }
    public static void error(String msg) { show(Alert.AlertType.ERROR, msg); }

    private static void show(Alert.AlertType type, String msg) {
        Runnable r = () -> {
            Alert a = new Alert(type);
            a.setTitle("Message");
            a.setHeaderText(null);
            a.setContentText(msg);
            a.showAndWait();
        };
        if (Platform.isFxApplicationThread()) r.run(); else Platform.runLater(r);
        // keeps your current code behavior but safe if called off-thread
    }
}
