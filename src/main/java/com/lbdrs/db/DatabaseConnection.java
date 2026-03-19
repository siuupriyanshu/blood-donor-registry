package com.lbdrs.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager.
 * Reads credentials from db.properties so they are never hard-coded.
 */
public class DatabaseConnection {

    private static Connection instance;

    private static String url;
    private static String user;
    private static String password;

    static {
        Properties props = new Properties();
        try (InputStream is = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException("db.properties not found on classpath");
            }
            props.load(is);
            url      = props.getProperty("db.url");
            user     = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    private DatabaseConnection() {}   // prevent external instantiation

    /**
     * Returns the single shared {@link Connection}.
     * Creates a new connection if one has not yet been established or was closed.
     */
    public static synchronized Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(url, user, password);
        }
        return instance;
    }

    /** Convenience method: close the connection (call on application shutdown). */
    public static synchronized void close() {
        if (instance != null) {
            try {
                instance.close();
            } catch (SQLException ignored) {}
            instance = null;
        }
    }
}
