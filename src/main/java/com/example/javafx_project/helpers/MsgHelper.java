package com.example.javafx_project.helpers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class MsgHelper {
    private MsgHelper() {}

    // Non-blocking info/warn/error (safe during animations/timer)
    public static void info(String msg)  { show(Alert.AlertType.INFORMATION, msg); }
    public static void warn(String msg)  { show(Alert.AlertType.WARNING, msg); }
    public static void error(String msg) { show(Alert.AlertType.ERROR, msg); }

    private static void show(Alert.AlertType t, String msg) {
        Platform.runLater(() -> {
            var a = new Alert(t, msg);
            a.show(); // NOTE: show(), not showAndWait()
        });
    }

    // Blocking confirm for normal UI button flows (not from Timeline)
    public static boolean confirmNow(String title, String msg) {
        var a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        a.setTitle(title);
        return a.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }
}
