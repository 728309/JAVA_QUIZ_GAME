package com.example.javafx_project.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public final class ViewLoader {
    private ViewLoader() {}

    public static void switchTo(Stage stage, String fxmlPath, String title, double w, double h) throws Exception {
        // fxmlPath should be absolute, e.g. "/com/example/javafx_project/Menu.fxml"
        URL fxmlUrl = ViewLoader.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new IllegalStateException("FXML not found: " + fxmlPath);
        }

        FXMLLoader fxml = new FXMLLoader(fxmlUrl);
        Parent root = fxml.load();

        Scene scene = new Scene(root, w, h);

        // Add stylesheet (absolute path). Change the filename if yours is style.css instead.
        String cssPath = "/com/example/javafx_project/css/fashion.css";
        URL cssUrl = ViewLoader.class.getResource(cssPath);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Stylesheet not found: " + cssPath); // avoids NPE, app still runs
        }

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
