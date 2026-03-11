package com.blooddonor.dao;

import com.blooddonor.models.BloodType;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for BloodType.
 * Handles CRUD operations for blood type records in the database.
 */
public class BloodTypeDAO {
    private Connection connection;

    /**
     * Constructor that initializes the connection.
     *
     * @throws SQLException if database connection fails
     */
    public BloodTypeDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Creates a new blood type record in the database.
     *
     * @param bloodType The BloodType object to create
     * @return The ID of the created blood type, or -1 if creation failed
     */
    public int create(BloodType bloodType) {
        String sql = "INSERT INTO blood_types (type, description) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, bloodType.getType());
            stmt.setString(2, bloodType.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating blood type: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves a blood type by its ID.
     *
     * @param id The blood type ID
     * @return The BloodType object, or null if not found
     */
    public BloodType read(int id) {
        String sql = "SELECT id, type, description, created_at FROM blood_types WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBloodType(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading blood type: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all blood types from the database.
     *
     * @return A list of all BloodType objects
     */
    public List<BloodType> findAll() {
        List<BloodType> bloodTypes = new ArrayList<>();
        String sql = "SELECT id, type, description, created_at FROM blood_types ORDER BY type";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bloodTypes.add(mapResultSetToBloodType(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all blood types: " + e.getMessage());
        }
        return bloodTypes;
    }

    /**
     * Finds a blood type by its type string (e.g., "A+").
     *
     * @param type The blood type string
     * @return The BloodType object, or null if not found
     */
    public BloodType findByType(String type) {
        String sql = "SELECT id, type, description, created_at FROM blood_types WHERE type = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBloodType(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding blood type by type: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates an existing blood type record.
     *
     * @param bloodType The BloodType object with updated values
     * @return true if update was successful, false otherwise
     */
    public boolean update(BloodType bloodType) {
        String sql = "UPDATE blood_types SET type = ?, description = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bloodType.getType());
            stmt.setString(2, bloodType.getDescription());
            stmt.setInt(3, bloodType.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating blood type: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a blood type record by its ID.
     *
     * @param id The blood type ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM blood_types WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting blood type: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a BloodType object.
     *
     * @param rs The ResultSet
     * @return The mapped BloodType object
     * @throws SQLException if column access fails
     */
    private BloodType mapResultSetToBloodType(ResultSet rs) throws SQLException {
        BloodType bloodType = new BloodType();
        bloodType.setId(rs.getInt("id"));
        bloodType.setType(rs.getString("type"));
        bloodType.setDescription(rs.getString("description"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            bloodType.setCreatedAt(createdAt.toLocalDateTime());
        }
        return bloodType;
    }
}
