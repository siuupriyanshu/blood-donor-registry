package com.blooddonor.controller;

import com.blooddonor.service.SearchService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import java.sql.SQLException;

/**
 * Controller for the Dashboard view.
 * Displays statistics and reports about donors.
 */
public class DashboardController {

    @FXML
    private Label totalDonorsLabel;

    @FXML
    private Label availableDonorsLabel;

    @FXML
    private Label totalDonationsLabel;

    @FXML
    private ListView<String> bloodTypeListView;

    @FXML
    private ListView<String> locationListView;

    private SearchService searchService;

    /**
     * Initializes the dashboard controller and loads statistics.
     */
    @FXML
    public void initialize() {
        try {
            searchService = new SearchService();
            refreshStatistics();
        } catch (SQLException e) {
            showError("Database Error", "Failed to initialize dashboard: " + e.getMessage());
        }
    }

    /**
     * Refreshes all statistics displayed on the dashboard.
     */
    @FXML
    private void refreshStatistics() {
            int totalDonors = searchService.getAllDonors().size();
            int availableDonors = (int) searchService.getAllDonors().stream()
                    .filter(d -> d.isAvailability())
                    .count();

            totalDonorsLabel.setText(String.valueOf(totalDonors));
            availableDonorsLabel.setText(String.valueOf(availableDonors));

            bloodTypeListView.getItems().clear();
            for (SearchService.BloodTypeReport report : searchService.getDonorCountByBloodType()) {
                bloodTypeListView.getItems().add(report.toString());
        }

            locationListView.getItems().clear();
            for (SearchService.LocationReport report : searchService.getDonorCountByLocation()) {
                locationListView.getItems().add(report.toString());
            }

            totalDonationsLabel.setText("0");
    }

    /**
     * Shows an error alert dialog.
     *
     * @param title The alert title
     * @param message The error message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
