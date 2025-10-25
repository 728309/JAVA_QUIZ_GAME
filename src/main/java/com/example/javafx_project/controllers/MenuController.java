package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.GameManager;
import com.example.javafx_project.helpers.ViewLoader;
import com.example.javafx_project.model.Quiz;
import com.example.javafx_project.service.GameLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MenuController {

    @FXML private Label welcomeLabel;
    @FXML private Button startBtn;

    @FXML
    public void initialize() {
        var gm = GameManager.get();
        if (gm.getPlayer() != null) {
            welcomeLabel.setText("Logged in as: " + gm.getPlayer().getFullname());
        } else {
            welcomeLabel.setText("Not signed in");
        }
        startBtn.setDisable(!gm.readyToStart());
    }

    @FXML
    void onLoginClick(ActionEvent e) throws Exception {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Login.fxml", "Login", 420, 260);
    }

    @FXML
    void onLoadQuizClick(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        fc.setTitle("Select quiz JSON");
        File file = fc.showOpenDialog(((Node)e.getSource()).getScene().getWindow());
        if (file == null) return;

        try {
            Quiz quiz = GameLoader.loadFromFile(file);
            GameManager.get().setQuiz(quiz);
            showInfo("Loaded quiz: " + quiz.getTitle() + " (" + quiz.getQuestions().size() + " questions)");
            startBtn.setDisable(!GameManager.get().readyToStart());
        } catch (Exception ex) {
            showError("Failed to load quiz:\n" + ex.getMessage());
        }
    }

    @FXML
    void onStartQuizClick(ActionEvent e) throws Exception {
        if (!GameManager.get().readyToStart()) {
            showError("Please login and load a quiz first.");
            return;
        }
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Game.fxml", "Quiz", 640, 420);
    }

    @FXML
    void onViewResultsClick(ActionEvent e) throws Exception {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Results.fxml", "Results", 560, 420);
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
