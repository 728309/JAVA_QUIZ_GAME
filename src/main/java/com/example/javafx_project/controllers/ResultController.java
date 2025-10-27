package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.GameManager;
import com.example.javafx_project.helpers.MsgHelper;

import com.example.javafx_project.helpers.Navigator;
import com.example.javafx_project.helpers.PathHelper;

import com.example.javafx_project.model.Result;
import com.example.javafx_project.service.ResultService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

public class ResultController {
    @FXML private Label quizNameLabel;
    @FXML private Label yourScoreLabel;
    @FXML private Label completedMsgLabel;

    @FXML private TableView<Result> table;
    @FXML private TableColumn<Result, String> colPlayer, colScore, colPct, colDate, colPoints;

    private Path savedFile;
    private final ObservableList<Result> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        bindColumns();
        table.setItems(items);

        var gm = GameManager.get();
        var quiz = gm.getQuiz();
        var player = gm.getPlayer();

        if (quiz == null) {
            quizNameLabel.setText("No quiz loaded");
            yourScoreLabel.setText("");
            completedMsgLabel.setText("Open history requires a quiz context.");
            return;
        }

        quizNameLabel.setText("Quiz name: " + quiz.getTitle());

        // compute current session score as percentage (even in practice; we just won't save)
        int total = gm.total();
        int correct = gm.getCorrect();
        int pct = (total == 0) ? 0 : (int)Math.round(100.0 * correct / total);
        yourScoreLabel.setText("Your score: " + pct + "%");

        // Completed HTML message (simple templating)
        String msg = quiz.getCompletedHtml();
        msg = msg.replace("{correctAnswers}", String.valueOf(correct))
                .replace("{questionCount}", String.valueOf(total))
                .replaceAll("<[^>]+>", ""); // strip HTML tags for Label
        completedMsgLabel.setText(msg.isBlank() ? " " : msg);

        // SAVE only in normal mode
        if (!gm.isPracticeMode()) {
            var latest = new Result(
                    quiz.getQuizId(),
                    quiz.getTitle(),
                    (player == null ? "Anonymous" : player.getFullname()),
                    total, correct, gm.getPoints(),
                    OffsetDateTime.now().toString()
            );
            ResultService.append(latest);
            if (latest.getCorrect() == latest.getTotal() && latest.getTotal() > 0) {
                com.example.javafx_project.helpers.MsgHelper.info("🎉 Perfect score! You answered everything correctly.");
            }

            savedFile = ResultService.fileFor(quiz.getQuizId());
        }

        // Load + sort leaderboard (descending by percentage)
        loadLeaderboardSorted(quiz.getQuizId());

        // reset only progress (keep practice flag as set until user returns)
        gm.resetProgress();
    }

    private void bindColumns() {
        colPlayer.setCellValueFactory(c -> s(c.getValue().getPlayerName()));
        colScore.setCellValueFactory(c -> s(c.getValue().getCorrect() + " / " + c.getValue().getTotal()));
        colPct.setCellValueFactory(c -> {
            double pct = c.getValue().getTotal() == 0 ? 0 : (100.0 * c.getValue().getCorrect() / c.getValue().getTotal());
            return s(String.format("%.0f%%", pct));
        });
        if (colPoints != null) {
            colPoints.setCellValueFactory(c -> s(String.format("%.2f", c.getValue().getPoints())));
        }
        colDate.setCellValueFactory(c -> s(c.getValue().getDate()));
    }

    private javafx.beans.property.SimpleStringProperty s(String v) {
        return new javafx.beans.property.SimpleStringProperty(v);
    }

    private void loadLeaderboardSorted(String quizId) {
        List<Result> all = ResultService.readAll(quizId);

        // Sort by percentage desc, then date desc (optional)
        all.sort(Comparator
                .comparingDouble((Result r) -> (r.getTotal() == 0 ? 0.0 : (100.0 * r.getCorrect() / r.getTotal())))
                .reversed()
                .thenComparing(Result::getDate, Comparator.nullsLast(Comparator.reverseOrder()))
        );
        items.setAll(all);
    }

    @FXML
    public void onBack(ActionEvent e) throws Exception {
        Navigator.go(Navigator.stageOf(e), PathHelper.MENU, "Quiz Game — Menu", 480, 320);
    }

    @FXML
    public void onOpenFile(ActionEvent e) {
        try {
            if (savedFile == null && !items.isEmpty()) {
                savedFile = ResultService.fileFor(items.get(0).getQuizId());
            }
            if (savedFile == null) { MsgHelper.info("No results file yet."); return; }
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(savedFile.toFile());
            else MsgHelper.info("Saved file: " + savedFile.toAbsolutePath());
        } catch (IOException ex) {
            MsgHelper.error("Cannot open file: " + ex.getMessage());
        }
    }

    @FXML
    public void onRefresh(ActionEvent e) {
        if (!items.isEmpty()) loadLeaderboardSorted(items.get(0).getQuizId());
    }

    // === EXPORT CSV ===
    @FXML
    public void onExportCsv(ActionEvent e) {
        if (items.isEmpty()) {
            MsgHelper.warn("No records to export.");
            return;
        }
        String quizId = items.get(0).getQuizId();
        String quizName = items.get(0).getQuizName();

        FileChooser fc = new FileChooser();
        fc.setTitle("Export Leaderboard");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        fc.setInitialFileName("leaderboard.csv");
        var file = fc.showSaveDialog(Navigator.stageOf(e));
        if (file == null) return;

        // Format: quizId;quizName;playerName;totalQuestions;correctQuestions;date
        StringBuilder sb = new StringBuilder();
        for (Result r : items) {
            sb.append(escape(quizId)).append(';')
                    .append(escape(quizName)).append(';')
                    .append(escape(r.getPlayerName())).append(';')
                    .append(r.getTotal()).append(';')
                    .append(r.getCorrect()).append(';')
                    .append(escape(r.getDate()))
                    .append('\n');
        }
        try {
            java.nio.file.Files.writeString(file.toPath(), sb.toString(), StandardCharsets.UTF_8);
            MsgHelper.info("Exported: " + file.getAbsolutePath());
        } catch (IOException ex) {
            MsgHelper.error("Failed to export: " + ex.getMessage());
        }
    }

    private static String escape(String s) {
        if (s == null) return "";

        // keep it simple; CSV is semicolon-delimited — no need to escape commas.
        return s.replace("\n", " ").replace("\r", " ");
    }
}
