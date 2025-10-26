package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.GameManager;
import com.example.javafx_project.helpers.Navigator;
import com.example.javafx_project.helpers.Msg;
import com.example.javafx_project.helpers.Paths;
import com.example.javafx_project.model.CombiQuestion;
import com.example.javafx_project.model.Question;
import com.example.javafx_project.service.TimeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GameController {
    private static final int SEGMENTS = 12;

    @FXML private Label titleLabel, progressLabel, scoreLabel, timerLabel;
    @FXML private ProgressBar timerBar;          // optional; ok if null in FXML
    @FXML private VBox radioBox;
    @FXML private Button nextBtn;
    @FXML private Button submitBtn;
    @FXML private javafx.scene.layout.HBox timerBlocks;


    private final ToggleGroup group = new ToggleGroup();
    private final GameManager gm = GameManager.get();

    private String selectedRadio = null;
    private TimeService timer;
    private boolean answered = false;

    @FXML
    public void initialize() {
        showCurrentQuestion();
        updateHeader();
    }

    // ---------- UI / Header

    private void updateHeader() {
        progressLabel.setText((gm.currentIndex() + 1) + " / " + gm.total());
        scoreLabel.setText(String.format("Score: %d • Points: %.2f", gm.getCorrect(), gm.getPoints()));
    }

    private void showCurrentQuestion() {
        var q = gm.currentQuestion();
        titleLabel.setText(q.getTitle());
        answered = false;
        nextBtn.setDisable(true);
        if (submitBtn != null) submitBtn.setDisable(false);

        // clear previous radios
        group.getToggles().clear();
        radioBox.getChildren().removeIf(n -> n instanceof RadioButton);
        selectedRadio = null;

        // render everything as radio (boolean -> "True"/"False")
        renderRadioChoices(choicesOf(q));

        // timer + neon blocks
        setupTimer(q.getTimeLimit());
        buildTimerBlocks();
        updateBlocks(q.getTimeLimit(), q.getTimeLimit());

        setDefaultButtons(false);
    }

    private List<String> choicesOf(Question q) {
        return (q instanceof CombiQuestion cq) ? cq.getChoices() : List.of("True", "False");
    }

    private void renderRadioChoices(List<String> choices) {
        // insert before the last child if that's the Submit button, else append
        int childCount = radioBox.getChildren().size();
        int insertAt = (childCount > 0 && radioBox.getChildren().get(childCount - 1) instanceof Button)
                ? childCount - 1 : childCount;

        for (String choice : choices) {
            RadioButton rb = new RadioButton(choice);
            rb.setToggleGroup(group);
            rb.setOnAction(e -> selectedRadio = choice);
            radioBox.getChildren().add(insertAt++, rb);
        }
    }

    private void setDefaultButtons(boolean nextActive) {
        if (submitBtn != null) submitBtn.setDefaultButton(!nextActive);
        if (nextBtn   != null) nextBtn.setDefaultButton(nextActive);
    }

    // ---------- Timer

    private void setupTimer(int limit) {
        stopTimer();
        if (limit > 0) {
            timerLabel.setText(limit + "s");
            if (timerBar != null) timerBar.setProgress(1.0);

            timer = new TimeService(
                    limit,
                    left -> {
                        timerLabel.setText(left + "s");
                        if (timerBar != null) timerBar.setProgress(left / (double) limit);
                        updateBlocks(left, limit);
                    },
                    () -> {
                        if (!answered) {
                            Msg.info("⏰ Time's up!");
                            finalizeQuestion(false, limit, limit);
                        }
                    }
            );
            timer.start();
        } else {
            timer = null;
            timerLabel.setText("Untimed");
            if (timerBar != null) timerBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            if (timerBlocks != null) timerBlocks.getChildren().clear();
        }
    }

    private void stopTimer() {
        if (timer != null) { timer.stop(); timer = null; }
    }

    private int secondsUsedFor(int limit) {
        if (limit <= 0 || timer == null) return 0;
        return Math.max(0, limit - timer.getLeft());
    }

    private void buildTimerBlocks() {
        if (timerBlocks == null) return;

        if (!timerBlocks.getStyleClass().contains("timer-blocks")) {
            timerBlocks.getStyleClass().add("timer-blocks");
        }
        if (timerBlocks.getChildren().size() == SEGMENTS) {
            // reset to "off"
            timerBlocks.getChildren().forEach(n ->
                    ((javafx.scene.layout.Region) n).getStyleClass().remove("on"));
            return;
        }
        timerBlocks.getChildren().clear();
        for (int i = 0; i < SEGMENTS; i++) {
            var r = new javafx.scene.layout.Region();
            r.getStyleClass().add("seg");
            r.setPrefSize(18, 10);
            timerBlocks.getChildren().add(r);
        }
    }

    private void updateBlocks(int left, int limit) {
        if (timerBlocks == null || limit <= 0) return;
        if (timerBlocks.getChildren().size() != SEGMENTS) buildTimerBlocks();

        int lit = (int) Math.ceil((left / (double) limit) * SEGMENTS);
        for (int i = 0; i < SEGMENTS; i++) {
            var r = (javafx.scene.layout.Region) timerBlocks.getChildren().get(i);
            r.getStyleClass().remove("on");
            if (i < lit) r.getStyleClass().add("on");
        }
    }

    // ---------- Answers

    @FXML
    void onSubmitRadio(ActionEvent e) {
        if (selectedRadio == null) { Msg.warn("Please select an option."); return; }
        checkAnswer(selectedRadio);
    }
    @FXML void onAnswerTrue(ActionEvent e)  { checkAnswer(Boolean.TRUE); }   // kept for safety
    @FXML void onAnswerFalse(ActionEvent e) { checkAnswer(Boolean.FALSE); }  // kept for safety

    private void checkAnswer(Object answer) {
        Question q = gm.currentQuestion();

        // normalize boolean radio values ("True"/"False") to Boolean
        if (!(q instanceof CombiQuestion) && answer instanceof String s) {
            String v = s.trim().toLowerCase();
            if (v.equals("true") || v.equals("t") || v.equals("yes") || v.equals("y") || v.equals("1")) {
                answer = Boolean.TRUE;
            } else if (v.equals("false") || v.equals("f") || v.equals("no") || v.equals("n") || v.equals("0")) {
                answer = Boolean.FALSE;
            }
        }

        boolean isCorrect = q.isCorrect(answer);
        int limit = q.getTimeLimit();
        int used  = secondsUsedFor(limit);
        stopTimer();
        finalizeQuestion(isCorrect, limit, used);
    }

    private void finalizeQuestion(boolean correct, int limit, int used) {
        answered = true;
        if (submitBtn != null) submitBtn.setDisable(true);

        if (correct) {
            gm.addCorrect();
            double pts = (limit > 0) ? (1.0 - (used / (double) limit)) : 1.0;
            gm.addPoints(Math.max(0, Math.min(1, pts))); // clamp
            Msg.info(String.format(" Correct! +%.2f pts", pts));
        } else {
            Msg.info(" Incorrect. +0.00 pts");
        }

        updateHeader();
        boolean hasNext = gm.hasNext();
        nextBtn.setDisable(!gm.hasNext());
        setDefaultButtons(hasNext);

        if (!gm.hasNext()) goResults();
    }

    // ---------- Nav

    @FXML
    void onNext(ActionEvent e) {
        stopTimer();
        if (gm.hasNext()) {
            gm.goNext();
            showCurrentQuestion();
            updateHeader();
        }
        nextBtn.setDisable(!gm.hasNext());
    }

    @FXML
    void onBack(ActionEvent e) throws Exception {
        stopTimer();
        Navigator.go(Navigator.stageOf(e), Paths.MENU, "Quiz Game — Menu", 480, 320);
    }

    private void goResults() {
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            Navigator.go(stage, Paths.RESULTS, "Results", 560, 420);
        } catch (Exception ex) {
            Msg.error(ex.getMessage());
        }
    }
}
