package com.example.javafx_project.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class ViewLoader {
    private ViewLoader() {}

    public static void switchTo(Stage stage, String fxmlPath, String title, double w, double h) throws Exception {
        FXMLLoader fxml = new FXMLLoader(ViewLoader.class.getResource(fxmlPath));
        Parent root = fxml.load();
        stage.setTitle(title);
        stage.setScene(new Scene(root, w, h));
        stage.show();
    }
}