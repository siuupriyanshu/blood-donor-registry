package com.lbdrs.controller;

import com.lbdrs.dao.DonationHistoryRepository;
import com.lbdrs.model.Donor;
import com.lbdrs.model.DonationHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for donation_history.fxml.
 * Medical Staff and Admin can view and record donations.
 */
public class DonationController {

    @FXML private Label   donorLabel;

    // History table
    @FXML private TableView<DonationHistory>            historyTable;
    @FXML private TableColumn<DonationHistory, Integer> colHistId;
    @FXML private TableColumn<DonationHistory, String>  colDate;
    @FXML private TableColumn<DonationHistory, String>  colStatus;
    @FXML private TableColumn<DonationHistory, String>  colLocation;

    // New donation form
    @FXML private DatePicker  donationDatePicker;
    @FXML private ComboBox<String> statusBox;
    @FXML private TextField   locationField;
    @FXML private Label       formError;

    private final DonationHistoryRepository histRepo = new DonationHistoryRepository();
    private Donor donor;
    private ObservableList<DonationHistory> histData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colHistId.setCellValueFactory(new PropertyValueFactory<>("historyId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("donationDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("donationStatus"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        statusBox.setItems(FXCollections.observableArrayList(
                "Completed", "Deferred", "Cancelled"));
        statusBox.getSelectionModel().selectFirst();

        donationDatePicker.setValue(LocalDate.now());
        historyTable.setItems(histData);
        formError.setVisible(false);
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
        donorLabel.setText("Donor: " + donor.getName() +
                           " | Blood Group: " + donor.getBloodGroup());
        loadHistory();
    }

    private void loadHistory() {
        try {
            List<DonationHistory> list = histRepo.getHistoryByDonor(donor.getDonorId());
            histData.setAll(list);
        } catch (SQLException e) {
            showError("Load error: " + e.getMessage());
        }
    }

    @FXML
    public void handleRecordDonation() {
        LocalDate date = donationDatePicker.getValue();
        String    loc  = locationField.getText().trim();

        if (date == null) { showError("Please select a donation date."); return; }
        if (date.isAfter(LocalDate.now())) { showError("Date cannot be in the future."); return; }
        if (loc.isEmpty()) { showError("Location is required."); return; }

        DonationHistory h = new DonationHistory();
        h.setDonorId(donor.getDonorId());
        h.setDonationDate(date);
        h.setDonationStatus(statusBox.getValue());
        h.setLocation(loc);

        try {
            histRepo.addHistory(h);
            locationField.clear();
            donationDatePicker.setValue(LocalDate.now());
            formError.setVisible(false);
            loadHistory();
        } catch (SQLException e) {
            showError("Save error: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        formError.setText(msg);
        formError.setVisible(true);
    }
}
