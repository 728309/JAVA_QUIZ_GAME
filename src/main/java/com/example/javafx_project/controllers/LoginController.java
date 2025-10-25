package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.Session;
import com.example.javafx_project.model.Player;
import com.example.javafx_project.service.UserService;
import com.example.javafx_project.helpers.ViewLoader;
import com.example.javafx_project.helpers.GameManager;// if you created it earlier
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    public void onLogin(ActionEvent e) throws Exception {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();

        if (u.isEmpty() || p.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }

        Player player = UserService.authenticate(u, p);
        if (player == null) {
            messageLabel.setText("Invalid credentials.");
            return;
        }

        Session.setCurrent(player);
        GameManager.get().setPlayer(player);
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Welcome, " + player.getFullname() + "!");

        // navigate to Menu (or straight to Game if you prefer)
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Menu.fxml",
                "Quiz Game — Menu", 480, 320);
    }

    @FXML
    public void onBack(ActionEvent e) throws Exception {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        ViewLoader.switchTo(stage, "/com/example/javafx_project/Menu.fxml",
                "Quiz Game — Menu", 480, 320);
    }
}
