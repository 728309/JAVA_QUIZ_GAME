package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.Theme;
import com.example.javafx_project.helpers.GameManager;
import com.example.javafx_project.helpers.Navigator;
import com.example.javafx_project.helpers.MsgHelper;
import com.example.javafx_project.helpers.PathHelper;
import com.example.javafx_project.model.Quiz;
import com.example.javafx_project.service.GameLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        welcomeLabel.setText(gm.getPlayer() == null
                ? "Not signed in"
                : "Hi, " + gm.getPlayer().getFullname() + " 👋");
        startBtn.setDisable(!gm.readyToStart());
    }

    @FXML
    void onLoginClick(ActionEvent e) throws Exception {
        Stage stage = Navigator.stageOf(e);
        Navigator.go(stage, PathHelper.LOGIN, "Login", 420, 260);
    }

    @FXML
    void onLoadQuizClick(ActionEvent e) {
        Stage stage = Navigator.stageOf(e);

        FileChooser fc = new FileChooser();
        fc.setTitle("Select quiz JSON");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fc.showOpenDialog(stage);
        if (file == null) return;

        try {
            Quiz quiz = GameLoader.loadFromFile(file);
            GameManager.get().setQuiz(quiz);

            MsgHelper.info("Loaded: " + quiz.getTitle() + "  ("
                    + quiz.getQuestions().size() + " questions)");
            startBtn.setDisable(!GameManager.get().readyToStart());

            // optional: if you want a default accent after loading, keep this; else delete:
            // Theme.setAccent(stage.getScene(), "#ff58d1");
        } catch (Exception ex) {
            MsgHelper.error("Failed to load quiz:\n" + ex.getMessage());
        }
    }

    @FXML
    void onStartQuizClick(ActionEvent e) throws Exception {
        if (!GameManager.get().readyToStart()) {
            MsgHelper.warn("Login and load a quiz first.");
            return;
        }
        boolean ok = MsgHelper.confirmNow(
                "Before you start",
                "Once you submit an answer you can’t go back to change it. Continue?"
        );
        if (!ok) return;

        var stage = Navigator.stageOf(e);
        Navigator.go(stage, PathHelper.GAME, "Quiz", 640, 420);
    }


    @FXML
    void onViewResultsClick(ActionEvent e) throws Exception {
        Stage stage = Navigator.stageOf(e);
        Navigator.go(stage, PathHelper.RESULTS, "Results", 560, 420);
    }

    @FXML
    void onThemeCyan(ActionEvent e) {
        Stage stage = Navigator.stageOf(e);
        Theme.setAccent(stage.getScene(), "#00e5ff");
        stage.getScene().getRoot().getStyleClass().setAll("root", "cyan");
    }

    @FXML
    void onThemePink(ActionEvent e) {
        Stage stage = Navigator.stageOf(e);
        Theme.setAccent(stage.getScene(), "#ff58d1");
        stage.getScene().getRoot().getStyleClass().setAll("root", "pink");
    }


}
