package com.blooddonor.controller;

import com.blooddonor.models.Donor;
import com.blooddonor.models.BloodType;
import com.blooddonor.models.Location;
import com.blooddonor.service.DonorService;
import com.blooddonor.service.SearchService;
import com.blooddonor.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for the Donor Management view.
 * Handles CRUD operations for donors.
 */
public class DonorManagementController {

    @FXML
    public VBox root;

    @FXML
    private ComboBox<BloodType> filterBloodTypeCombo;

    @FXML
    private ComboBox<Location> filterLocationCombo;

    @FXML
    private CheckBox filterAvailabilityCheck;

    @FXML
    private TableView<Donor> donorTableView;

    @FXML
    private TableColumn<Donor, Integer> idColumn;

    @FXML
    private TableColumn<Donor, String> nameColumn;

    @FXML
    private TableColumn<Donor, String> emailColumn;

    @FXML
    private TableColumn<Donor, String> phoneColumn;

    @FXML
    private TableColumn<Donor, String> bloodTypeColumn;

    @FXML
    private TableColumn<Donor, String> locationColumn;

    @FXML
    private TableColumn<Donor, String> lastDonationColumn;

    @FXML
    private TableColumn<Donor, String> availabilityColumn;

    private DonorService donorService;
    private SearchService searchService;

    /**
     * Initializes the donor management controller.
     */
    @FXML
    public void initialize() {
        try {
            donorService = new DonorService();
            searchService = new SearchService();
            setupTableColumns();
            loadFiltersAndData();
        } catch (SQLException e) {
            showError("Database Error", "Failed to initialize: " + e.getMessage());
        }
    }

    /**
     * Sets up the table columns with cell value factories.
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        phoneColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhone()));
        bloodTypeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBloodType().getType()));
        locationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLocation().toString()));
        lastDonationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(DateUtils.formatDate(cellData.getValue().getLastDonationDate())));
        availabilityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().isAvailability() ? "Yes" : "No"));
    }

    /**
     * Loads filter options and refreshes donor list.
     */
    private void loadFiltersAndData() {
            filterBloodTypeCombo.setItems(FXCollections.observableArrayList(searchService.getAllBloodTypes()));
            filterBloodTypeCombo.getItems().add(0, new BloodType(0, "All Blood Types", ""));
            filterBloodTypeCombo.getSelectionModel().selectFirst();

            filterLocationCombo.setItems(FXCollections.observableArrayList(searchService.getAllLocations()));
            filterLocationCombo.getItems().add(0, new Location(0, "All Locations", "", ""));
            filterLocationCombo.getSelectionModel().selectFirst();

            refreshDonorList();
    }

    /**
     * Refreshes the donor list from the database.
     */
    @FXML
    private void refreshDonorList() {
            List<Donor> donors = donorService.getAllDonors();
            ObservableList<Donor> observableDonors = FXCollections.observableArrayList(donors);
            donorTableView.setItems(observableDonors);
    }

    /**
     * Applies filters to the donor list.
     */
    @FXML
    private void applyFilters() {
        try {
            BloodType selectedBloodType = filterBloodTypeCombo.getValue();
            Location selectedLocation = filterLocationCombo.getValue();
            boolean availabilityOnly = filterAvailabilityCheck.isSelected();

            List<Donor> filteredDonors = donorService.getAllDonors();

            // Apply blood type filter
            if (selectedBloodType != null && selectedBloodType.getId() > 0) {
                final BloodType bloodType = selectedBloodType;
                filteredDonors = filteredDonors.stream()
                        .filter(d -> d.getBloodType().getId() == bloodType.getId())
                        .toList();
            }

            // Apply location filter
            if (selectedLocation != null && selectedLocation.getId() > 0) {
                final Location location = selectedLocation;
                filteredDonors = filteredDonors.stream()
                        .filter(d -> d.getLocation().getId() == location.getId())
                        .toList();
            }

            // Apply availability filter
            if (availabilityOnly) {
                filteredDonors = filteredDonors.stream()
                        .filter(Donor::isAvailability)
                        .toList();
            }

            donorTableView.setItems(FXCollections.observableArrayList(filteredDonors));
        } catch (Exception e) {
            showError("Filter Error", "Failed to apply filters: " + e.getMessage());
        }
    }

    /**
     * Clears all filters and shows all donors.
     */
    @FXML
    private void clearFilters() {
        filterBloodTypeCombo.getSelectionModel().selectFirst();
        filterLocationCombo.getSelectionModel().selectFirst();
        filterAvailabilityCheck.setSelected(false);
        refreshDonorList();
    }

    /**
     * Handles creation of a new donor.
     */
    @FXML
    private void handleCreateDonor() {
        showNotImplemented("Create Donor feature");
    }

    /**
     * Handles update of selected donor.
     */
    @FXML
    private void handleUpdateDonor() {
        Donor selected = donorTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a donor to update");
            return;
        }
        showNotImplemented("Update Donor feature");
    }

    /**
     * Handles deletion of selected donor.
     */
    @FXML
    private void handleDeleteDonor() {
        Donor selected = donorTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a donor to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Donor: " + selected.getName());
        confirmAlert.setContentText("Are you sure you want to delete this donor?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (donorService.deleteDonor(selected.getId())) {
                showInfo("Success", "Donor deleted successfully");
                refreshDonorList();
            } else {
                showError("Error", "Failed to delete donor");
            }
        }
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
