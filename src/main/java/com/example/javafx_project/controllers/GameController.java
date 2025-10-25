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

    @FXML private Node boolBox; // HBox, but we can keep it as Node for visibility toggling

    private String selectedRadio = null; // track selection for radio
    private final ToggleGroup group = new ToggleGroup();

    @FXML
    public void initialize() {
        // Load first question
        renderCurrent();
        updateHeader();
    }

    private void updateHeader() {
        var gm = GameManager.get();
        progressLabel.setText((gm.currentIndex()+1) + " / " + gm.total());
        scoreLabel.setText("Score: " + gm.getCorrect());
    }

    private void renderCurrent() {
        var gm = GameManager.get();
        Question q = gm.currentQuestion();
        titleLabel.setText(q.getTitle());

        // Reset UI state
        nextBtn.setDisable(true);
        selectedRadio = null;
        group.getToggles().clear();
        radioBox.getChildren().removeIf(n -> n instanceof RadioButton); // remove old radios

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
        if (correct) {
            gm.addCorrect();
            new Alert(Alert.AlertType.INFORMATION, " Correct!").showAndWait();
        } else {
            new Alert(Alert.AlertType.INFORMATION, " Incorrect.").showAndWait();
        }
        updateHeader();
        nextBtn.setDisable(!gm.hasNext());
        // If this was the last question, go to results
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
}
