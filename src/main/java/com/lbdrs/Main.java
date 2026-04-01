package com.lbdrs;

import com.lbdrs.db.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application entry point.
 * Launches the JavaFX login screen.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("Local Blood Donor Registry System");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Clean up DB connection on shutdown
        DatabaseConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
