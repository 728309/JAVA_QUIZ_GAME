package com.example.javafx_project.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class Navigator {
    private Navigator() {}

    public static void go(Stage stage, String fxml, String title, double w, double h) throws Exception {
        URL fxmlUrl = Navigator.class.getResource(fxml);
        if (fxmlUrl == null) throw new IllegalStateException("FXML not found: " + fxml);

        // capture old theme (inline + style classes)
        Scene old = stage.getScene();
        String oldInline = null;
        List<String> oldClasses = List.of();
        if (old != null && old.getRoot() != null) {
            oldInline = old.getRoot().getStyle();
            oldClasses = new ArrayList<>(old.getRoot().getStyleClass());
        }

        Parent root = new FXMLLoader(fxmlUrl).load();
        Scene scene = new Scene(root, w, h);

        // re-apply theme
        if (oldInline != null && !oldInline.isBlank()) root.setStyle(oldInline);
        if (oldClasses.isEmpty()) {
            root.getStyleClass().setAll("root"); // make sure root class is present
        } else {
            if (!oldClasses.contains("root")) oldClasses.add(0, "root");
            root.getStyleClass().setAll(oldClasses);
        }

        URL css = Navigator.class.getResource(Paths.CSS);
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public static Stage stageOf(javafx.event.ActionEvent e) {
        return (Stage)((javafx.scene.Node)e.getSource()).getScene().getWindow();
    }
}
