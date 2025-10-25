package com.example.javafx_project.helpers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class GoBack {
        void back(ActionEvent e) throws Exception {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Menu.fxml", "Quiz Game — Menu", 480, 320);
    }
}
