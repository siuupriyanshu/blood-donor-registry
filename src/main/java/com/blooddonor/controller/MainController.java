package com.blooddonor.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert;

/**
 * Controller for the main application window.
 * Handles menu actions and tab navigation.
 */
public class MainController {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Label statusLabel;

    @FXML
    private Label connectionLabel;

    private DashboardController dashboardController;
    private DonorManagementController donorManagementController;
    private SearchController searchController;
    private DonationHistoryController donationHistoryController;

    /**
     * Initializes the main controller and loads sub-controllers.
     */
    @FXML
    public void initialize() {
        // Initialize sub-controllers can be done here if needed
        updateConnectionStatus();
    }

    /**
     * Handles exit action from File menu.
     */
    @FXML
    private void handleExit() {
        Platform.exit();
    }

    /**
     * Handles about action from Help menu.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Local Blood Donor Registry");
        alert.setHeaderText("Local Blood Donor Registry System");
        alert.setContentText("Version: 1.0.0\n\n" +
                "A desktop application for managing blood donors and donations.\n\n" +
                "Technologies: JavaFX, MySQL, JDBC");
        alert.showAndWait();
    }

    /**
     * Returns the main tab pane so sub-controllers can inject their content.
     */
    public TabPane getMainTabPane() {
        return mainTabPane;
    }

    /**
     * Updates the connection status label.
     */
    public void updateConnectionStatus() {
        // This will be called when connection is verified
        statusLabel.setText("Ready");
        connectionLabel.setText("Connected");
        connectionLabel.setStyle("-fx-text-fill: green;");
    }

    /**
     * Updates the status label with a message.
     *
     * @param message The message to display
     */
    public void setStatus(String message) {
        statusLabel.setText(message);
    }
}
