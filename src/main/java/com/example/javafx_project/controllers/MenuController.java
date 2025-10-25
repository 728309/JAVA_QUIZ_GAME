package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.ViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    void onLoadQuizClick(ActionEvent e) throws Exception {
        // next step will implement FileChooser; for now just go to Login to keep flow working
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Login.fxml", "Login", 420, 260);
    }

    @FXML
    void onStartQuizClick(ActionEvent e) throws Exception {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Game.fxml", "Quiz", 640, 420);
    }

    @FXML
    void onViewResultsClick(ActionEvent e) throws Exception {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Results.fxml", "Results", 560, 380);
    }
}
