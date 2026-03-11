package com.blooddonor.controller;

import com.blooddonor.models.Donor;
import com.blooddonor.models.DonationRecord;
import com.blooddonor.service.DonorService;
import com.blooddonor.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for the Donation History view.
 * Handles viewing and managing donation records for donors.
 */
public class DonationHistoryController {

    @FXML
    public VBox root;

    @FXML
    private ComboBox<Donor> donorComboBox;

    @FXML
    private Label totalDonationsCountLabel;

    @FXML
    private Label totalVolumeLabel;

    @FXML
    private Label lastDonationLabel;

    @FXML
    private TableView<DonationRecord> donationHistoryTableView;

    @FXML
    private TableColumn<DonationRecord, Integer> recordIdColumn;

    @FXML
    private TableColumn<DonationRecord, String> recordDateColumn;

    @FXML
    private TableColumn<DonationRecord, Integer> recordVolumeColumn;

    @FXML
    private TableColumn<DonationRecord, String> recordStatusColumn;

    @FXML
    private TableColumn<DonationRecord, String> recordNotesColumn;

    private DonorService donorService;
    private Donor selectedDonor;

    /**
     * Initializes the donation history controller.
     */
    @FXML
    public void initialize() {
        try {
            donorService = new DonorService();
            setupTableColumns();
            loadDonors();
        } catch (SQLException e) {
            showError("Database Error", "Failed to initialize: " + e.getMessage());
        }
    }

    /**
     * Sets up the table columns.
     */
    private void setupTableColumns() {
        recordIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        recordDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(DateUtils.formatDate(cellData.getValue().getDonationDate())));
        recordVolumeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getVolumeCollected()).asObject());
        recordStatusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        recordNotesColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNotes()));
    }

    /**
     * Loads all donors into the combo box.
     */
    private void loadDonors() {
            List<Donor> donors = donorService.getAllDonors();
            donorComboBox.setItems(FXCollections.observableArrayList(donors));
            donorComboBox.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Donor object) {
                    return object == null ? "" : object.getName() + " - " + object.getBloodType().getType();
                }

                @Override
                public Donor fromString(String string) {
                    return null;
                }
            });
    }

    /**
     * Loads donation history for the selected donor.
     */
    @FXML
    private void loadDonationHistory() {
        selectedDonor = donorComboBox.getValue();
        if (selectedDonor == null) {
            showWarning("No Selection", "Please select a donor");
            return;
        }

            List<DonationRecord> records = donorService.getDonationHistory(selectedDonor.getId());
            donationHistoryTableView.setItems(FXCollections.observableArrayList(records));

            int totalDonations = donorService.getTotalDonations(selectedDonor.getId());
            int totalVolume = donorService.getTotalVolumeDonated(selectedDonor.getId());
            String lastDonation = selectedDonor.getLastDonationDate() != null ?
                    DateUtils.formatDate(selectedDonor.getLastDonationDate()) : "Never";

            totalDonationsCountLabel.setText(String.valueOf(totalDonations));
            totalVolumeLabel.setText(String.valueOf(totalVolume));
            lastDonationLabel.setText(lastDonation);
    }

    /**
     * Handles adding a new donation record.
     */
    @FXML
    private void handleAddDonation() {
        if (selectedDonor == null) {
            showWarning("No Selection", "Please select a donor first");
            return;
        }
        showNotImplemented("Add Donation feature");
    }

    /**
     * Handles deleting a donation record.
     */
    @FXML
    private void handleDeleteRecord() {
        DonationRecord selected = donationHistoryTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a donation record to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Donation Record");
        confirmAlert.setContentText("Are you sure you want to delete this record?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            // Delete operation would be implemented here
            showNotImplemented("Delete Donation Record feature");
        }
    }

    /**
     * Refreshes the donation history.
     */
    @FXML
    private void refreshHistory() {
        loadDonationHistory();
    }

    /**
     * Shows an information alert.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a warning alert.
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error alert.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a not implemented message.
     */
    private void showNotImplemented(String feature) {
        showWarning("Not Implemented", feature + " will be implemented soon");
    }
}
