package com.blooddonor.controller;

import com.blooddonor.models.Donor;
import com.blooddonor.service.DonorService;
import com.blooddonor.util.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller for the Donation Recorder dialog.
 * Handles recording new donation donations for donors.
 */
public class DonationRecorderController {

    @FXML
    private ComboBox<Donor> donorCombo;

    @FXML
    private DatePicker donationDatePicker;

    @FXML
    private Spinner<Integer> volumeSpinner;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TextArea notesArea;

    @FXML
    private Label infoLabel;

    private DonorService donorService;
    private Stage stage;

    /**
     * Initializes the donation recorder controller.
     */
    @FXML
    public void initialize() {
        try {
            donorService = new DonorService();
            setupComboBoxes();
        } catch (SQLException e) {
            showError("Database Error", "Failed to initialize: " + e.getMessage());
        }
    }

    /**
     * Sets up combo box options.
     */
    private void setupComboBoxes() {
            var donors = donorService.getAllDonors();
            donorCombo.setItems(FXCollections.observableArrayList(donors));
            donorCombo.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Donor object) {
                    return object == null ? "" : object.getName() + " - " + object.getBloodType().getType();
                }

                @Override
                public Donor fromString(String string) {
                    return null;
                }
            });

            statusCombo.setItems(FXCollections.observableArrayList("Completed", "Pending", "Failed", "Cancelled"));
            statusCombo.getSelectionModel().select(0);

            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 550, 450);
            volumeSpinner.setValueFactory(valueFactory);

            donationDatePicker.setValue(LocalDate.now());
    }

    /**
     * Handles the donation recording action.
     */
    @FXML
    private void handleRecordDonation() {
            Donor selectedDonor = donorCombo.getValue();
            LocalDate donationDate = donationDatePicker.getValue();
            int volume = volumeSpinner.getValue();
            String status = statusCombo.getValue();

            if (selectedDonor == null) {
                showError("Validation Error", "Please select a donor");
                return;
            }

            if (donationDate == null) {
                showError("Validation Error", "Please select a donation date");
                return;
            }

            if (!ValidationUtils.isValidDonationVolume(volume)) {
                showError("Validation Error", "Volume must be between 400 and 550 ml");
                return;
            }

            if (status == null) {
                showError("Validation Error", "Please select a status");
                return;
            }

            if (donorService.recordDonation(selectedDonor.getId(), donationDate, volume, status)) {
                showInfo("Success", "Donation recorded successfully");
                closeDialog();
            } else {
                showError("Error", "Failed to record donation");
        }
    }

    /**
     * Handles the cancel action.
     */
    @FXML
    private void handleCancel() {
        closeDialog();
    }

    /**
     * Closes the dialog.
     */
    private void closeDialog() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Sets the stage for this dialog.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
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
     * Shows an error alert.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
