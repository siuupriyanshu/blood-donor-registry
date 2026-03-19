package com.lbdrs.controller;

import com.lbdrs.dao.DonorRepository;
import com.lbdrs.model.Donor;
import com.lbdrs.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller for donor_form.fxml – used for both Register and Edit.
 */
public class DonorFormController {

    @FXML private Label        formTitle;
    @FXML private TextField    nameField;
    @FXML private TextField    ageField;
    @FXML private ComboBox<String> genderBox;
    @FXML private ComboBox<String> bloodGroupBox;
    @FXML private TextField    addressField;
    @FXML private TextField    locationField;
    @FXML private TextField    contactField;
    @FXML private DatePicker   lastDonationDate;
    @FXML private CheckBox     availableCheck;
    @FXML private Label        errorLabel;

    private final DonorRepository donorRepo = new DonorRepository();
    private Donor          existingDonor;

    @FXML
    public void initialize() {
        genderBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        bloodGroupBox.setItems(FXCollections.observableArrayList(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        errorLabel.setVisible(false);
    }

    /** Called when editing an existing donor. Pass null for registration. */
    public void setDonor(Donor donor) {
        this.existingDonor = donor;
        if (donor != null) {
            formTitle.setText("Edit Donor");
            nameField.setText(donor.getName());
            ageField.setText(String.valueOf(donor.getAge()));
            genderBox.setValue(donor.getGender());
            bloodGroupBox.setValue(donor.getBloodGroup());
            addressField.setText(donor.getAddress());
            locationField.setText(donor.getLocation());
            contactField.setText(donor.getContactNumber());
            if (donor.getLastDonationDate() != null)
                lastDonationDate.setValue(donor.getLastDonationDate());
            availableCheck.setSelected(donor.isAvailable());
        } else {
            formTitle.setText("Register New Donor");
            availableCheck.setSelected(true);
        }
    }

    @FXML
    public void handleSave() {
        String name    = nameField.getText();
        String ageStr  = ageField.getText();
        String bg      = bloodGroupBox.getValue();
        String contact = contactField.getText();

        String error = ValidationUtil.validateDonor(name, ageStr, bg, contact);
        if (error != null) { showError(error); return; }

        Donor d = existingDonor != null ? existingDonor : new Donor();
        d.setName(name.trim());
        d.setAge(Integer.parseInt(ageStr.trim()));
        d.setGender(genderBox.getValue());
        d.setBloodGroup(bg);
        d.setAddress(addressField.getText().trim());
        d.setLocation(locationField.getText().trim());
        d.setContactNumber(contact.trim());
        LocalDate ld = lastDonationDate.getValue();
        d.setLastDonationDate(ld);
        d.setAvailable(availableCheck.isSelected());

        try {
            if (existingDonor == null) {
                donorRepo.insertDonor(d);
            } else {
                donorRepo.updateDonor(d);
            }
            closeForm();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
