package com.blooddonor.controller;

import com.blooddonor.models.Donor;
import com.blooddonor.models.BloodType;
import com.blooddonor.models.Location;
import com.blooddonor.service.SearchService;
import com.blooddonor.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for the Search & Filter view.
 * Handles searching for donors by blood type and location.
 */
public class SearchController {

    @FXML
    public VBox root;

    @FXML
    private ComboBox<BloodType> searchBloodTypeCombo;

    @FXML
    private ComboBox<Location> searchLocationCombo;

    @FXML
    private CheckBox searchAvailabilityCheck;

    @FXML
    private TableView<Donor> searchResultsTableView;

    @FXML
    private TableColumn<Donor, Integer> resultIdColumn;

    @FXML
    private TableColumn<Donor, String> resultNameColumn;

    @FXML
    private TableColumn<Donor, String> resultEmailColumn;

    @FXML
    private TableColumn<Donor, String> resultPhoneColumn;

    @FXML
    private TableColumn<Donor, String> resultBloodTypeColumn;

    @FXML
    private TableColumn<Donor, String> resultLocationColumn;

    @FXML
    private TableColumn<Donor, String> resultDaysUntilEligibleColumn;

    @FXML
    private Label resultCountLabel;

    @FXML
    private Label availableCountLabel;

    private SearchService searchService;

    /**
     * Initializes the search controller.
     */
    @FXML
    public void initialize() {
        try {
            searchService = new SearchService();
            setupTableColumns();
            loadSearchFilters();
        } catch (SQLException e) {
            showError("Database Error", "Failed to initialize search: " + e.getMessage());
        }
    }

    /**
     * Sets up the table columns.
     */
    private void setupTableColumns() {
        resultIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        resultNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        resultEmailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        resultPhoneColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhone()));
        resultBloodTypeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBloodType().getType()));
        resultLocationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLocation().toString()));
        resultDaysUntilEligibleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(DateUtils.getDaysUntilEligible(cellData.getValue().getLastDonationDate()))
        ));
    }

    /**
     * Loads search filter options.
     */
    private void loadSearchFilters() {
            searchBloodTypeCombo.setItems(FXCollections.observableArrayList(searchService.getAllBloodTypes()));
            searchBloodTypeCombo.getItems().add(0, new BloodType(0, "All Blood Types", ""));
            searchBloodTypeCombo.getSelectionModel().selectFirst();

            searchLocationCombo.setItems(FXCollections.observableArrayList(searchService.getAllLocations()));
            searchLocationCombo.getItems().add(0, new Location(0, "All Locations", "", ""));
            searchLocationCombo.getSelectionModel().selectFirst();
    }

    /**
     * Performs the search based on selected criteria.
     */
    @FXML
    private void performSearch() {
            BloodType selectedBloodType = searchBloodTypeCombo.getValue();
            Location selectedLocation = searchLocationCombo.getValue();
            boolean eligibleOnly = searchAvailabilityCheck.isSelected();

            int bloodTypeId = (selectedBloodType != null && selectedBloodType.getId() > 0) ? selectedBloodType.getId() : -1;
            int locationId = (selectedLocation != null && selectedLocation.getId() > 0) ? selectedLocation.getId() : -1;

            List<Donor> results;
            if (eligibleOnly) {
                results = searchService.searchEligibleDonors(bloodTypeId, locationId);
            } else {
                if (bloodTypeId > 0 && locationId > 0) {
                    results = searchService.searchByBloodTypeAndLocation(bloodTypeId, locationId);
                } else if (bloodTypeId > 0) {
                    results = searchService.searchByBloodType(bloodTypeId);
                } else if (locationId > 0) {
                    results = searchService.searchByLocation(locationId);
            } else {
                    results = searchService.getAllBloodTypes().isEmpty() ?
                            List.of() : searchService.searchByBloodType(searchService.getAllBloodTypes().get(0).getId());
            }
        }

            searchResultsTableView.setItems(FXCollections.observableArrayList(results));
            resultCountLabel.setText(String.valueOf(results.size()));

            long availableCount = results.stream()
                    .filter(d -> d.isAvailability() && DateUtils.isEligibleToDonate(d.getLastDonationDate()))
                    .count();
            availableCountLabel.setText(String.valueOf(availableCount));
    }

    /**
     * Clears the search fields.
     */
    @FXML
    private void clearSearchFields() {
        searchBloodTypeCombo.getSelectionModel().selectFirst();
        searchLocationCombo.getSelectionModel().selectFirst();
        searchAvailabilityCheck.setSelected(false);
        searchResultsTableView.getItems().clear();
        resultCountLabel.setText("0");
        availableCountLabel.setText("0");
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
