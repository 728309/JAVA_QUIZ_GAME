package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.GameManager;
import com.example.javafx_project.helpers.Navigator;
import com.example.javafx_project.helpers.Msg;
import com.example.javafx_project.helpers.Paths;
import com.example.javafx_project.model.Result;
import com.example.javafx_project.service.ResultService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

public class ResultController {
    @FXML private Label summaryLabel;
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

        if (quiz != null) {
            var latest = new Result(
                    quiz.getQuizId(),
                    quiz.getTitle(),
                    (player == null ? "Anonymous" : player.getFullname()),
                    gm.total(), gm.getCorrect(), gm.getPoints(),
                    OffsetDateTime.now().toString()
            );
            ResultService.append(latest);
            gm.reset();

            savedFile = ResultService.fileFor(quiz.getQuizId());
            summaryLabel.setText(String.format(
                    "Latest: %s • %s • %d/%d (%.2f pts)",
                    latest.getPlayerName(), latest.getQuizName(),
                    latest.getCorrect(), latest.getTotal(), latest.getPoints()
            ));

            loadHistory(quiz.getQuizId());
        } else {
            summaryLabel.setText("No recent game. Opened history view.");
        }
    }

    private void bindColumns() {
        colPlayer.setCellValueFactory(c -> s(c.getValue().getPlayerName()));
        colScore.setCellValueFactory(c -> s(c.getValue().getCorrect() + " / " + c.getValue().getTotal()));
        colPct.setCellValueFactory(c -> {
            double pct = c.getValue().getTotal() == 0 ? 0 : (100.0 * c.getValue().getCorrect() / c.getValue().getTotal());
            return s(String.format("%.0f%%", pct));
        });
        colDate.setCellValueFactory(c -> s(c.getValue().getDate()));
        colPoints.setCellValueFactory(c -> s(String.format("%.2f", c.getValue().getPoints())));
    }

    private static SimpleStringProperty s(String v) {
        return new SimpleStringProperty(v);
    }

    private void loadHistory(String quizId) {
        List<Result> all = ResultService.readAll(quizId);
        items.setAll(all);
    }

    @FXML
    public void onBack(ActionEvent e) throws Exception {
        Navigator.go(Navigator.stageOf(e), Paths.MENU, "Quiz Game — Menu", 480, 320);
    }

    @FXML
    public void onOpenFile(ActionEvent e) {
        try {
            if (savedFile == null && !items.isEmpty()) {
                savedFile = ResultService.fileFor(items.get(0).getQuizId());
            }
            if (savedFile == null) { Msg.info("No results file yet. Play a quiz first."); return; }

            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(savedFile.toFile());
            else Msg.info("Saved file: " + savedFile.toAbsolutePath());
        } catch (IOException ex) {
            Msg.error("Cannot open file: " + ex.getMessage());
        }
    }

    @FXML
    public void onRefresh(ActionEvent e) {
        if (!items.isEmpty()) loadHistory(items.get(0).getQuizId());
    }
}
