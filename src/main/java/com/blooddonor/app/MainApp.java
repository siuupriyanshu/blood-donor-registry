package com.blooddonor.app;

import com.blooddonor.controller.*;
import com.blooddonor.dao.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Main application entry point for the Blood Donor Registry System.
 * Initializes the JavaFX application and loads the primary stage.
 */
public class MainApp extends Application {

    private MainController mainController;
    private DatabaseConnection dbConnection;

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage The primary stage window
     * @throws Exception if application startup fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Initialize database connection
            initializeDatabase();

            // Load FXML files
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            BorderPane mainRoot = mainLoader.load();
            mainController = mainLoader.getController();

            // Load sub-controllers
            loadSubControllers(mainController.getMainTabPane());

            // Create scene and set stage
            Scene scene = new Scene(mainRoot, 1200, 700);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Local Blood Donor Registry System");
            primaryStage.setOnCloseRequest(event -> shutdown());

            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initializes the database connection.
     */
    private void initializeDatabase() throws SQLException {
        try {
            dbConnection = DatabaseConnection.getInstance();
            if (dbConnection.isConnected()) {
                System.out.println("Database connection established successfully");
                if (mainController != null) {
                    mainController.updateConnectionStatus();
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Loads all sub-controllers for the tabs.
     *
     * @param tabPane The tab pane to load controllers into
     */
    private void loadSubControllers(TabPane tabPane) {
        try {
            // Load Dashboard
            FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            dashboardLoader.load();
            DashboardController dashboardController = dashboardLoader.getController();
            tabPane.getTabs().get(0).setContent(dashboardLoader.getRoot());

            // Load Donor Management
            FXMLLoader donorMgmtLoader = new FXMLLoader(getClass().getResource("/fxml/donor_management.fxml"));
            donorMgmtLoader.load();
            DonorManagementController donorMgmtController = donorMgmtLoader.getController();
            tabPane.getTabs().get(1).setContent(donorMgmtLoader.getRoot());

            // Load Search
            FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/fxml/search.fxml"));
            searchLoader.load();
            SearchController searchController = searchLoader.getController();
            tabPane.getTabs().get(2).setContent(searchLoader.getRoot());

            // Load Donation History
            FXMLLoader donationHistoryLoader = new FXMLLoader(getClass().getResource("/fxml/donation_history.fxml"));
            donationHistoryLoader.load();
            DonationHistoryController donationHistoryController = donationHistoryLoader.getController();
            tabPane.getTabs().get(3).setContent(donationHistoryLoader.getRoot());

        } catch (IOException e) {
            System.err.println("Failed to load sub-controllers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles application shutdown.
     */
    private void shutdown() {
        if (dbConnection != null) {
            dbConnection.closeConnection();
        }
        System.out.println("Application closed");
    }
}
