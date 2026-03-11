package com.blooddonor.controller;

import com.blooddonor.models.Donor;
import com.blooddonor.models.BloodType;
import com.blooddonor.models.Location;
import com.blooddonor.service.DonorService;
import com.blooddonor.service.SearchService;
import com.blooddonor.util.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller for the Donor Editor dialog.
 * Handles creation and editing of donor records.
 */
public class DonorEditorController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<BloodType> bloodTypeCombo;

    @FXML
    private DatePicker dateOfBirthPicker;

    @FXML
    private ComboBox<String> genderCombo;

    @FXML
    private ComboBox<Location> locationCombo;

    @FXML
    private CheckBox availabilityCheck;

    @FXML
    private DatePicker lastDonationPicker;

    @FXML
    private Label infoLabel;

    private SearchService searchService;
    private DonorService donorService;
    private Donor donorToEdit;
    private Stage stage;
    private boolean isEditMode = false;

    /**
     * Initializes the donor editor controller.
     */
    @FXML
    public void initialize() {
        try {
            searchService = new SearchService();
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
            var bloodTypes = searchService.getAllBloodTypes();
            bloodTypeCombo.setItems(FXCollections.observableArrayList(bloodTypes));

            genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));

            var locations = searchService.getAllLocations();
            locationCombo.setItems(FXCollections.observableArrayList(locations));
            locationCombo.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Location object) {
                    return object == null ? "" : object.getCity() + ", " + object.getState();
                }

                @Override
                public Location fromString(String string) {
                    return null;
                }
            });
    }

    /**
     * Sets the donor to edit (if in edit mode).
     *
     * @param donor The donor to edit, or null for creation
     */
    public void setDonor(Donor donor) {
        this.donorToEdit = donor;
        if (donor != null) {
            isEditMode = true;
            titleLabel.setText("Edit Donor");
            populateFields(donor);
        } else {
            isEditMode = false;
            titleLabel.setText("Create New Donor");
        }
    }

    /**
     * Populates form fields with donor data.
     */
    private void populateFields(Donor donor) {
        nameField.setText(donor.getName());
        emailField.setText(donor.getEmail());
        phoneField.setText(donor.getPhone());
        bloodTypeCombo.setValue(donor.getBloodType());
        dateOfBirthPicker.setValue(donor.getDateOfBirth());
        genderCombo.setValue(donor.getGender());
        locationCombo.setValue(donor.getLocation());
        availabilityCheck.setSelected(donor.isAvailability());
        if (donor.getLastDonationDate() != null) {
            lastDonationPicker.setValue(donor.getLastDonationDate());
        }
    }

    /**
     * Handles the save action.
     */
    @FXML
    private void handleSave() {
        try {
            if (!validateInputs()) {
                return;
            }

            Donor donor = new Donor();
            if (isEditMode) {
                donor.setId(donorToEdit.getId());
            }

            donor.setName(nameField.getText().trim());
            donor.setEmail(emailField.getText().trim());
            donor.setPhone(phoneField.getText().trim());
            donor.setBloodType(bloodTypeCombo.getValue());
            donor.setDateOfBirth(dateOfBirthPicker.getValue());
            donor.setGender(genderCombo.getValue());
            donor.setLocation(locationCombo.getValue());
            donor.setAvailability(availabilityCheck.isSelected());
            if (lastDonationPicker.getValue() != null) {
                donor.setLastDonationDate(lastDonationPicker.getValue());
            }

            if (isEditMode) {
                if (donorService.updateDonor(donor)) {
                    showInfo("Success", "Donor updated successfully");
                    closeDialog();
                } else {
                    showError("Error", "Failed to update donor");
                }
            } else {
                int id = donorService.createDonor(donor);
                if (id > 0) {
                    showInfo("Success", "Donor created successfully");
                    closeDialog();
                } else {
                    showError("Error", "Failed to create donor");
                }
            }
        } catch (IllegalArgumentException e) {
            showError("Validation Error", e.getMessage());
        }
    }

    /**
     * Validates all form inputs.
     */
    private boolean validateInputs() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        LocalDate dob = dateOfBirthPicker.getValue();
        String gender = genderCombo.getValue();
        BloodType bloodType = bloodTypeCombo.getValue();
        Location location = locationCombo.getValue();

        if (!ValidationUtils.isValidName(name)) {
            showError("Validation Error", "Name must be between 2 and 150 characters");
            return false;
        }

        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            showError("Validation Error", "Invalid email format");
            return false;
        }

        if (!phone.isEmpty() && !ValidationUtils.isValidPhone(phone)) {
            showError("Validation Error", "Invalid phone format");
            return false;
        }

        if (dob == null || !ValidationUtils.isValidAge(dob)) {
            showError("Validation Error", "Age must be between 18 and 65 years");
            return false;
        }

        if (gender == null) {
            showError("Validation Error", "Please select a gender");
            return false;
        }

        if (bloodType == null) {
            showError("Validation Error", "Please select a blood type");
            return false;
        }

        if (location == null) {
            showError("Validation Error", "Please select a location");
            return false;
        }

        return true;
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
