package com.blooddonor.dao;

import com.blooddonor.models.MedicalCondition;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for MedicalCondition.
 * Handles CRUD operations for medical condition records in the database.
 */
public class MedicalConditionDAO {
    private Connection connection;

    /**
     * Constructor that initializes the connection.
     *
     * @throws SQLException if database connection fails
     */
    public MedicalConditionDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Creates a new medical condition record in the database.
     *
     * @param condition The MedicalCondition object to create
     * @return The ID of the created condition, or -1 if creation failed
     */
    public int create(MedicalCondition condition) {
        String sql = "INSERT INTO medical_conditions (donor_id, condition_name, diagnosis_date, notes) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, condition.getDonorId());
            stmt.setString(2, condition.getConditionName());

            if (condition.getDiagnosisDate() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(condition.getDiagnosisDate()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, condition.getNotes());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating medical condition: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves a medical condition by its ID.
     *
     * @param id The medical condition ID
     * @return The MedicalCondition object, or null if not found
     */
    public MedicalCondition read(int id) {
        String sql = "SELECT id, donor_id, condition_name, diagnosis_date, notes, created_at " +
                     "FROM medical_conditions WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicalCondition(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading medical condition: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all medical conditions for a specific donor.
     *
     * @param donorId The donor ID
     * @return A list of MedicalCondition objects for the donor
     */
    public List<MedicalCondition> findByDonorId(int donorId) {
        List<MedicalCondition> conditions = new ArrayList<>();
        String sql = "SELECT id, donor_id, condition_name, diagnosis_date, notes, created_at " +
                     "FROM medical_conditions WHERE donor_id = ? ORDER BY diagnosis_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, donorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    conditions.add(mapResultSetToMedicalCondition(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding medical conditions by donor: " + e.getMessage());
        }
        return conditions;
    }

    /**
     * Retrieves all medical conditions.
     *
     * @return A list of all MedicalCondition objects
     */
    public List<MedicalCondition> findAll() {
        List<MedicalCondition> conditions = new ArrayList<>();
        String sql = "SELECT id, donor_id, condition_name, diagnosis_date, notes, created_at " +
                     "FROM medical_conditions ORDER BY diagnosis_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                conditions.add(mapResultSetToMedicalCondition(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all medical conditions: " + e.getMessage());
        }
        return conditions;
    }

    /**
     * Updates an existing medical condition record.
     *
     * @param condition The MedicalCondition object with updated values
     * @return true if update was successful, false otherwise
     */
    public boolean update(MedicalCondition condition) {
        String sql = "UPDATE medical_conditions SET donor_id = ?, condition_name = ?, diagnosis_date = ?, notes = ? " +
                     "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, condition.getDonorId());
            stmt.setString(2, condition.getConditionName());

            if (condition.getDiagnosisDate() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(condition.getDiagnosisDate()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, condition.getNotes());
            stmt.setInt(5, condition.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating medical condition: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a medical condition record by its ID.
     *
     * @param id The medical condition ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM medical_conditions WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting medical condition: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a MedicalCondition object.
     *
     * @param rs The ResultSet
     * @return The mapped MedicalCondition object
     * @throws SQLException if column access fails
     */
    private MedicalCondition mapResultSetToMedicalCondition(ResultSet rs) throws SQLException {
        MedicalCondition condition = new MedicalCondition();
        condition.setId(rs.getInt("id"));
        condition.setDonorId(rs.getInt("donor_id"));
        condition.setConditionName(rs.getString("condition_name"));

        Date diagnosisDate = rs.getDate("diagnosis_date");
        if (diagnosisDate != null) {
            condition.setDiagnosisDate(diagnosisDate.toLocalDate());
        }

        condition.setNotes(rs.getString("notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            condition.setCreatedAt(createdAt.toLocalDateTime());
        }

        return condition;
    }
}
