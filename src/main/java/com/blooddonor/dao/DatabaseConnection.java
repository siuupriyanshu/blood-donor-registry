package com.blooddonor.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for managing database connections.
 * Provides a single connection to the MySQL database.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Database configuration - Update these values based on your MySQL setup
    private static final String URL = "jdbc:mysql://localhost:3306/blood_donor_registry";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /**
     * Private constructor to prevent instantiation.
     */
    private DatabaseConnection() {
    }

    /**
     * Gets the singleton instance of DatabaseConnection.
     * Creates the connection if it doesn't exist or is closed.
     *
     * @return The DatabaseConnection instance
     * @throws SQLException if database connection fails
     */
    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
            instance.connect();
        } else if (instance.connection == null || instance.connection.isClosed()) {
            instance.connect();
        }
        return instance;
    }

    /**
     * Establishes connection to the database.
     *
     * @throws SQLException if connection fails
     */
    private void connect() throws SQLException {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Returns the active database connection.
     *
     * @return The Connection object
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Checks if the connection is active.
     *
     * @return true if connection is active, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
