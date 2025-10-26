package com.example.javafx_project.controllers;

import com.example.javafx_project.helpers.GameManager;
import com.example.javafx_project.helpers.Navigator;
import com.example.javafx_project.helpers.PathHelper;
import com.example.javafx_project.model.Player;
import com.example.javafx_project.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    public void onLogin(ActionEvent e) throws Exception {
        String u = usernameField.getText().trim();
        String p = passwordField.getText().trim();

        if (u.isEmpty() || p.isEmpty()) { message("Please enter username and password.", false); return; }

        Player player = UserService.authenticate(u, p);
        if (player == null) { message("Invalid credentials.", false); return; }

        GameManager.get().setPlayer(player);
        message("Welcome, " + player.getFullname() + "!", true);

        Navigator.go(Navigator.stageOf(e), PathHelper.MENU, "Quiz Game — Menu", 480, 320);
    }

    @FXML
    public void onBack(ActionEvent e) throws Exception {
        Navigator.go(Navigator.stageOf(e), PathHelper.MENU, "Quiz Game — Menu", 480, 320);
    }

    private void message(String text, boolean ok) {
        messageLabel.setStyle(ok ? "-fx-text-fill: #38b000;" : "-fx-text-fill: #ff4d4f;");
        messageLabel.setText(text);
    }
}
