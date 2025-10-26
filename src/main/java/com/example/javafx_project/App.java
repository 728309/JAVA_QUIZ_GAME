package com.example.javafx_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxml = new FXMLLoader(App.class.getResource("/com/example/javafx_project/Menu.fxml"));
        Parent root = fxml.load();

        Scene scene = new Scene(root, 480, 320);

        // attach CSS
        var cssUrl = App.class.getResource("/com/example/javafx_project/css/fashion.css");
        if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

        // ensure "root" style class present
        if (!root.getStyleClass().contains("root")) root.getStyleClass().add(0, "root");

        stage.setTitle("Quiz Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
