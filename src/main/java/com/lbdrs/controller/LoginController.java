package com.lbdrs.controller;

import com.lbdrs.dao.UserRepository;
import com.lbdrs.model.Session;
import com.lbdrs.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * MVC Controller for login.fxml
 */
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    private final UserRepository userRepo = new UserRepository();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        try {
            User user = userRepo.authenticate(username, password);
            if (user != null) {
                Session.getInstance().setCurrentUser(user);
                loadDashboard();
            } else {
                showError("Invalid username or password.");
                passwordField.clear();
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/dashboard.fxml"));
            Parent root  = loader.load();
            Stage  stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("LBDRS – Dashboard");
            stage.setScene(new Scene(root, 1100, 700));
            stage.centerOnScreen();
        } catch (IOException e) {
            showError("Could not load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
