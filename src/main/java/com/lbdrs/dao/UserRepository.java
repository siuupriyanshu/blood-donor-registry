package com.lbdrs.dao;

import com.lbdrs.db.DatabaseConnection;
import com.lbdrs.model.Admin;
import com.lbdrs.model.MedicalStaff;
import com.lbdrs.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

/**
 * DAO for the users table.
 * Handles authentication with BCrypt password verification.
 */
public class UserRepository {

    /**
     * Authenticates a user by username + password.
     * Returns the appropriate User subclass, or null on failure.
     */
    public User authenticate(String username, String rawPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password");
                // BCrypt comparison – works even if rawPassword came from a plain-text legacy row
                boolean matches;
                try {
                    matches = BCrypt.checkpw(rawPassword, hash);
                } catch (Exception e) {
                    // Fallback for seed rows that use plain-text passwords during dev
                    matches = rawPassword.equals(hash);
                }
                if (matches) {
                    String role   = rs.getString("role");
                    int    userId = rs.getInt("userId");
                    String uname  = rs.getString("username");
                    if ("Admin".equals(role)) {
                        return new Admin(userId, uname, hash, "SystemAdmin");
                    } else {
                        return new MedicalStaff(userId, uname, hash, "Staff");
                    }
                }
            }
        }
        return null;
    }

    /** Creates a new user with a BCrypt-hashed password. */
    public void createUser(String username, String rawPassword, String role) throws SQLException {
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
        String sql  = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, role);
            ps.executeUpdate();
        }
    }
}
