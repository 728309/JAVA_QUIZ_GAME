package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.GameManager;
import com.example.javafx_project.helpers.ViewLoader;
import com.example.javafx_project.model.Result;
import com.example.javafx_project.service.ResultService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

public class ResultController {

    @FXML private Label summaryLabel;
    @FXML private TableView<Result> table;
    @FXML private TableColumn<Result, String> colPlayer;
    @FXML private TableColumn<Result, String> colScore;
    @FXML private TableColumn<Result, String> colPct;
    @FXML private TableColumn<Result, String> colDate;
    @FXML private TableColumn<Result, String> colPoints;

    private Path savedFile;
    private final ObservableList<Result> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configure columns
        colPlayer.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPlayerName()));
        colScore.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getCorrect() + " / " + c.getValue().getTotal()));
        colPct.setCellValueFactory(c -> {
            double pct = (c.getValue().getTotal() == 0) ? 0.0 :
                    (100.0 * c.getValue().getCorrect() / c.getValue().getTotal());
            return new javafx.beans.property.SimpleStringProperty(String.format("%.0f%%", pct));
        });
        colDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDate()));
        colPoints.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.format("%.2f", c.getValue().getPoints())));
        table.setItems(items);

        var gm = GameManager.get();
        var quiz = gm.getQuiz();
        var player = gm.getPlayer();

        if (quiz != null) {
            String name = (player != null ? player.getFullname() : "Anonymous");

            // ⬇️ include points from GameManager
            Result latest = new Result(
                    quiz.getQuizId(),
                    quiz.getTitle(),
                    name,
                    gm.total(),
                    gm.getCorrect(),
                    gm.getPoints(),                       // <-- NEW
                    OffsetDateTime.now().toString()
            );

            ResultService.append(latest);
            gm.reset();                                   // reset for next run
            savedFile = ResultService.fileFor(quiz.getQuizId());

            summaryLabel.setText(String.format(
                    "Latest: %s • %s • %d/%d (%.2f pts)",
                    name, latest.getQuizName(), latest.getCorrect(), latest.getTotal(), latest.getPoints()
            ));

            loadHistory(quiz.getQuizId());
        } else {
            summaryLabel.setText("No recent game. Opened history view.");
        }
    }

    private void loadHistory(String quizId) {
        List<Result> all = ResultService.readAll(quizId);
        items.setAll(all);
    }

    @FXML
    public void onBack(ActionEvent e) throws Exception {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Menu.fxml",
                "Quiz Game — Menu", 480, 320);
    }

    @FXML
    public void onOpenFile(ActionEvent e) {
        try {
            if (savedFile == null && !items.isEmpty()) {
                savedFile = ResultService.fileFor(items.get(0).getQuizId());
            }
            if (savedFile == null) {
                new Alert(Alert.AlertType.INFORMATION, "No results file yet. Play a quiz first.").showAndWait();
                return;
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(savedFile.toFile());
            } else {
                new Alert(Alert.AlertType.INFORMATION,
                        "Saved file: " + savedFile.toAbsolutePath()).showAndWait();
            }
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot open file: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onRefresh(ActionEvent e) {
        if (!items.isEmpty()) {
            loadHistory(items.get(0).getQuizId());
        }
    }
}
