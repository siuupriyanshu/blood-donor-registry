package com.blooddonor.dao;

import com.blooddonor.models.DonationRecord;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for DonationRecord.
 * Handles CRUD operations for donation record entries in the database.
 */
public class DonationRecordDAO {
    private Connection connection;

    /**
     * Constructor that initializes the connection.
     *
     * @throws SQLException if database connection fails
     */
    public DonationRecordDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Creates a new donation record in the database.
     *
     * @param record The DonationRecord object to create
     * @return The ID of the created donation record, or -1 if creation failed
     */
    public int create(DonationRecord record) {
        String sql = "INSERT INTO donation_history (donor_id, donation_date, volume_collected, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, record.getDonorId());
            stmt.setDate(2, java.sql.Date.valueOf(record.getDonationDate()));
            stmt.setInt(3, record.getVolumeCollected());
            stmt.setString(4, record.getStatus());
            stmt.setString(5, record.getNotes());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating donation record: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves a donation record by its ID.
     *
     * @param id The donation record ID
     * @return The DonationRecord object, or null if not found
     */
    public DonationRecord read(int id) {
        String sql = "SELECT id, donor_id, donation_date, volume_collected, status, notes, created_at " +
                     "FROM donation_history WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDonationRecord(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading donation record: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all donation records for a specific donor.
     *
     * @param donorId The donor ID
     * @return A list of DonationRecord objects for the donor
     */
    public List<DonationRecord> findByDonorId(int donorId) {
        List<DonationRecord> records = new ArrayList<>();
        String sql = "SELECT id, donor_id, donation_date, volume_collected, status, notes, created_at " +
                     "FROM donation_history WHERE donor_id = ? ORDER BY donation_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, donorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToDonationRecord(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding donation records by donor: " + e.getMessage());
        }
        return records;
    }

    /**
     * Retrieves all donation records.
     *
     * @return A list of all DonationRecord objects
     */
    public List<DonationRecord> findAll() {
        List<DonationRecord> records = new ArrayList<>();
        String sql = "SELECT id, donor_id, donation_date, volume_collected, status, notes, created_at " +
                     "FROM donation_history ORDER BY donation_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(mapResultSetToDonationRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all donation records: " + e.getMessage());
        }
        return records;
    }

    /**
     * Finds donation records within a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of DonationRecord objects within the date range
     */
    public List<DonationRecord> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<DonationRecord> records = new ArrayList<>();
        String sql = "SELECT id, donor_id, donation_date, volume_collected, status, notes, created_at " +
                     "FROM donation_history WHERE donation_date BETWEEN ? AND ? ORDER BY donation_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToDonationRecord(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding donation records by date range: " + e.getMessage());
        }
        return records;
    }

    /**
     * Updates an existing donation record.
     *
     * @param record The DonationRecord object with updated values
     * @return true if update was successful, false otherwise
     */
    public boolean update(DonationRecord record) {
        String sql = "UPDATE donation_history SET donor_id = ?, donation_date = ?, volume_collected = ?, status = ?, notes = ? " +
                     "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, record.getDonorId());
            stmt.setDate(2, java.sql.Date.valueOf(record.getDonationDate()));
            stmt.setInt(3, record.getVolumeCollected());
            stmt.setString(4, record.getStatus());
            stmt.setString(5, record.getNotes());
            stmt.setInt(6, record.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating donation record: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a donation record by its ID.
     *
     * @param id The donation record ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM donation_history WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting donation record: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a DonationRecord object.
     *
     * @param rs The ResultSet
     * @return The mapped DonationRecord object
     * @throws SQLException if column access fails
     */
    private DonationRecord mapResultSetToDonationRecord(ResultSet rs) throws SQLException {
        DonationRecord record = new DonationRecord();
        record.setId(rs.getInt("id"));
        record.setDonorId(rs.getInt("donor_id"));
        record.setDonationDate(rs.getDate("donation_date").toLocalDate());
        record.setVolumeCollected(rs.getInt("volume_collected"));
        record.setStatus(rs.getString("status"));
        record.setNotes(rs.getString("notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            record.setCreatedAt(createdAt.toLocalDateTime());
        }

        return record;
    }
}
