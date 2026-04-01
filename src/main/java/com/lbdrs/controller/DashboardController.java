package com.lbdrs.controller;

import com.lbdrs.dao.DonorRepository;
import com.lbdrs.model.Donor;
import com.lbdrs.model.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Main dashboard controller – handles donor table, search, and navigation.
 */
public class DashboardController {

    // ── Toolbar ──────────────────────────────────────────────────────────────
    @FXML private Label    welcomeLabel;
    @FXML private Label    roleLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> bloodGroupFilter;
    @FXML private Button   addDonorBtn;
    @FXML private Button   addDonationBtn;

    // ── TableView ────────────────────────────────────────────────────────────
    @FXML private TableView<Donor>            donorTable;
    @FXML private TableColumn<Donor, Integer> colId;
    @FXML private TableColumn<Donor, String>  colName;
    @FXML private TableColumn<Donor, Integer> colAge;
    @FXML private TableColumn<Donor, String>  colGender;
    @FXML private TableColumn<Donor, String>  colBlood;
    @FXML private TableColumn<Donor, String>  colLocation;
    @FXML private TableColumn<Donor, String>  colContact;
    @FXML private TableColumn<Donor, Boolean> colAvailable;
    @FXML private TableColumn<Donor, Void>    colActions;

    @FXML private Label statusLabel;

    private final DonorRepository donorRepo = new DonorRepository();
    private ObservableList<Donor> donorData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Welcome header
        String uname = Session.getInstance().getCurrentUser().getUserName();
        String role  = Session.getInstance().getCurrentUser().getRole();
        welcomeLabel.setText("Welcome, " + uname);
        roleLabel.setText("[" + role + "]");

        // Admin-only controls
        addDonorBtn.setVisible(Session.getInstance().isAdmin());
        addDonationBtn.setVisible(Session.getInstance().canRecordDonation());

        // Blood group filter options
        bloodGroupFilter.setItems(FXCollections.observableArrayList(
                "All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        bloodGroupFilter.getSelectionModel().selectFirst();

        // Table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("donorId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colBlood.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
        colAvailable.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Boolean v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); }
                else {
                    setText(v ? "✔ Yes" : "✘ No");
                    setStyle(v ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                               : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });

        // Actions column
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn   = new Button("History");
            private final Button editBtn   = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                viewBtn.getStyleClass().add("btn-secondary");
                editBtn.getStyleClass().add("btn-warning");
                deleteBtn.getStyleClass().add("btn-danger");
                viewBtn.getStyleClass().add("table-action-btn");
                editBtn.getStyleClass().add("table-action-btn");
                deleteBtn.getStyleClass().add("table-action-btn");

                viewBtn.setOnAction(e -> openHistory(getTableRow().getItem()));
                editBtn.setOnAction(e -> openEdit(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> confirmDelete(getTableRow().getItem()));

                boolean isAdmin = Session.getInstance().isAdmin();
                editBtn.setVisible(isAdmin);
                deleteBtn.setVisible(isAdmin);
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); }
                else {
                    HBox box = new HBox(4, viewBtn, editBtn, deleteBtn);
                    box.setAlignment(javafx.geometry.Pos.CENTER);
                    box.setFillHeight(true);
                    setGraphic(box);
                }
            }
        });

        donorTable.setItems(donorData);
        loadDonors();
    }

    // ── Data loading ─────────────────────────────────────────────────────────
    private void loadDonors() {
        try {
            List<Donor> donors = donorRepo.getAllDonors();
            donorData.setAll(donors);
            statusLabel.setText(donors.size() + " donor(s) found.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String kw = searchField.getText().trim();
        String bg = bloodGroupFilter.getValue();
        try {
            List<Donor> results;
            if (!kw.isEmpty()) {
                results = donorRepo.searchDonors(kw);
            } else if (bg != null && !bg.equals("All")) {
                results = donorRepo.filterByBloodGroup(bg);
            } else {
                results = donorRepo.getAllDonors();
            }
            donorData.setAll(results);
            statusLabel.setText(results.size() + " donor(s) found.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Search Error", e.getMessage());
        }
    }

    @FXML
    public void handleSidebarSearch() {
        searchField.requestFocus();
        searchField.selectAll();
    }

    @FXML
    public void handleClear() {
        searchField.clear();
        bloodGroupFilter.getSelectionModel().selectFirst();
        loadDonors();
    }

    @FXML
    public void handleAddDonor() {
        openForm(null);
    }

    @FXML
    public void handleAddDonation() {
        Donor selectedDonor = donorTable.getSelectionModel().getSelectedItem();
        if (selectedDonor == null) {
            showAlert(Alert.AlertType.WARNING, "Select Donor",
                    "Please select a donor from the table first.");
            return;
        }
        openHistory(selectedDonor);
    }

    @FXML
    public void handleRefresh() {
        searchField.clear();
        bloodGroupFilter.getSelectionModel().selectFirst();
        loadDonors();
        statusLabel.setText("Refreshed.");
    }

    @FXML
    public void handleLogout() {
        Session.getInstance().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("LBDRS – Login");
            stage.setScene(new Scene(root, 600, 600));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Navigation helpers ───────────────────────────────────────────────────
    private void openForm(Donor donor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/donor_form.fxml"));
            Parent root = loader.load();
            DonorFormController ctrl = loader.getController();
            ctrl.setDonor(donor);

            Stage owner = (Stage) welcomeLabel.getScene().getWindow();
            Stage stage = new Stage();
            stage.setTitle(donor == null ? "Register New Donor" : "Edit Donor");
            stage.setScene(new Scene(root, owner.getWidth(), owner.getHeight()));
            stage.setMinWidth(900);
            stage.setMinHeight(650);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.showAndWait();
            loadDonors();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error", e.getMessage());
        }
    }

    private void openEdit(Donor donor) {
        if (donor != null) openForm(donor);
    }

    private void openHistory(Donor donor) {
        if (donor == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/donation_history.fxml"));
            Parent root = loader.load();
            DonationController ctrl = loader.getController();
            ctrl.setDonor(donor);

            Stage owner = (Stage) welcomeLabel.getScene().getWindow();
            Stage stage = new Stage();
            stage.setTitle("Donation History – " + donor.getName());
            stage.setScene(new Scene(root, owner.getWidth(), owner.getHeight()));
            stage.setMinWidth(900);
            stage.setMinHeight(650);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "History Error", e.getMessage());
        }
    }

    private void confirmDelete(Donor donor) {
        if (donor == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete donor \"" + donor.getName() + "\"? This cannot be undone.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                donorRepo.deleteDonor(donor.getDonorId());
                loadDonors();
                statusLabel.setText("Donor deleted.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Delete Error", e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
