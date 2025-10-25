package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.GameManager;
import com.example.javafx_project.helpers.ViewLoader;
import com.example.javafx_project.model.Answers;
import com.example.javafx_project.model.FullQuestion;
import com.example.javafx_project.model.Question;
import com.example.javafx_project.model.FullQuestion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameController {

    @FXML private Label titleLabel;
    @FXML private Label progressLabel;
    @FXML private Label scoreLabel;

    @FXML private VBox radioBox;
    @FXML private Button nextBtn;

    @FXML private Label timerLabel;
    @FXML private ProgressBar timerBar;

    @FXML private Node boolBox; // HBox, but we can keep it as Node for visibility toggling

    private String selectedRadio = null; // track selection for radio
    private final ToggleGroup group = new ToggleGroup();
    private javafx.animation.Timeline timeline;
    private int timeTotal = 0;
    private int timeLeft = 0;
    private boolean answeredThisQuestion = false;

    @FXML
    public void initialize() {
        // Load first question
        renderCurrent();
        updateHeader();
    }

    private void updateHeader() {
        var gm = GameManager.get();
        progressLabel.setText((gm.currentIndex()+1) + " / " + gm.total());
        scoreLabel.setText(String.format("Score: %d  •  Points: %.2f", gm.getCorrect(), gm.getPoints()));
    }

    private void renderCurrent() {
        var gm = GameManager.get();
        var q = gm.currentQuestion();
        titleLabel.setText(q.getTitle());

        // Reset UI state
        nextBtn.setDisable(true);
        selectedRadio = null;
        group.getToggles().clear();
        radioBox.getChildren().removeIf(n -> n instanceof RadioButton); // remove old radios

        // reset misc
        stopTimer();
        answeredThisQuestion = false;
        nextBtn.setDisable(true);
        selectedRadio = null;

        // clear old radios
        group.getToggles().clear();
        radioBox.getChildren().removeIf(n -> n instanceof RadioButton);

        // Show appropriate area
        boolean isRadio = q instanceof FullQuestion;
        radioBox.setVisible(isRadio); radioBox.setManaged(isRadio);
        boolBox.setVisible(!isRadio); boolBox.setManaged(!isRadio);

        if (isRadio) {
            FullQuestion rq = (FullQuestion) q;
            for (String choice : rq.getChoices()) {
                RadioButton rb = new RadioButton(choice);
                rb.setToggleGroup(group);
                rb.setOnAction(e -> selectedRadio = choice);
                radioBox.getChildren().add(radioBox.getChildren().size() - 1, rb); // before Submit button
            }
        }

        // start timer (if any)
        timeTotal = Math.max(0, q.getTimeLimit());
        if (timeTotal > 0) {
            timeLeft = timeTotal;
            timerLabel.setText(timeLeft + "s");
            timerBar.setProgress(1.0);
            startTimer();
        } else {
            timerLabel.setText("Untimed");
            timerBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        }
    }

    @FXML
    void onSubmitRadio(ActionEvent e) {
        if (selectedRadio == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an option.").showAndWait();
            return;
        }
        checkAnswerAndFeedback(selectedRadio);
    }

    @FXML
    void onAnswerTrue(ActionEvent e) {
        checkAnswerAndFeedback(Boolean.TRUE);
    }

    @FXML
    void onAnswerFalse(ActionEvent e) {
        checkAnswerAndFeedback(Boolean.FALSE);
    }

    private void checkAnswerAndFeedback(Object answer) {
        var gm = GameManager.get();
        boolean correct = gm.currentQuestion().isCorrect(answer);
        answeredThisQuestion = true;
        stopTimer();

        // compute seconds used
        int used = 0;
        int limit = gm.currentQuestion().getTimeLimit();
        if (limit > 0) used = Math.max(0, limit - timeLeft);

        double earned = 0.0;
        if (correct) {
            gm.addCorrect();
            if (limit > 0) {
                earned = 1.0 - (used / (double) limit);     // time-weighted
            } else {
                earned = 1.0;                                // untimed: full point
            }
            gm.addPoints(earned);
            new Alert(Alert.AlertType.INFORMATION,
                    String.format("✅ Correct! +%.2f pts", earned)).showAndWait();
        } else {
            new Alert(Alert.AlertType.INFORMATION, " Incorrect. +0.00 pts").showAndWait();
        }

        updateHeader();
        nextBtn.setDisable(!gm.hasNext());
        if (!gm.hasNext()) {
            goResults();
        }
    }

    @FXML
    void onNext(ActionEvent e) {
        var gm = GameManager.get();
        if (gm.hasNext()) {
            gm.goNext();
            renderCurrent();
            updateHeader();
        }
        nextBtn.setDisable(!gm.hasNext());
    }

    @FXML
    void onBack(ActionEvent e) {
        stopTimer();
        try {
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            ViewLoader.switchTo(stage, "/com/example/javafx_project/Menu.fxml",
                    "Quiz Game — Menu", 480, 320);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    private void goResults() {
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            ViewLoader.switchTo(stage, "/com/example/javafx_project/Results.fxml",
                    "Results", 560, 380);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    private void startTimer() {
        timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                    timeLeft--;
                    timerLabel.setText(timeLeft + "s");
                    timerBar.setProgress(timeLeft / (double) timeTotal);
                    if (timeLeft <= 0) {
                        stopTimer();
                        if (!answeredThisQuestion) {
                            // time up → auto-check as wrong (no points)
                            new Alert(Alert.AlertType.INFORMATION, "⏰ Time's up!").showAndWait();
                            nextBtn.setDisable(!GameManager.get().hasNext());
                            if (!GameManager.get().hasNext()) goResults();
                        }
                    }
                })
        );
        timeline.setCycleCount(timeTotal);
        timeline.playFromStart();
    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }
}
