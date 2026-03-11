package com.blooddonor.dao;

import com.blooddonor.models.Donor;
import com.blooddonor.models.BloodType;
import com.blooddonor.models.Location;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Donor.
 * Handles CRUD operations for donor records in the database.
 */
public class DonorDAO {
    private Connection connection;
    private BloodTypeDAO bloodTypeDAO;
    private LocationDAO locationDAO;

    /**
     * Constructor that initializes the connection and DAOs.
     *
     * @throws SQLException if database connection fails
     */
    public DonorDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.bloodTypeDAO = new BloodTypeDAO();
        this.locationDAO = new LocationDAO();
    }

    /**
     * Creates a new donor record in the database.
     *
     * @param donor The Donor object to create
     * @return The ID of the created donor, or -1 if creation failed
     */
    public int create(Donor donor) {
        String sql = "INSERT INTO donors (name, email, phone, blood_type_id, location_id, date_of_birth, gender, last_donation_date, availability) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, donor.getName());
            stmt.setString(2, donor.getEmail());
            stmt.setString(3, donor.getPhone());
            stmt.setInt(4, donor.getBloodType().getId());
            stmt.setInt(5, donor.getLocation().getId());
            stmt.setDate(6, java.sql.Date.valueOf(donor.getDateOfBirth()));
            stmt.setString(7, donor.getGender());

            if (donor.getLastDonationDate() != null) {
                stmt.setDate(8, java.sql.Date.valueOf(donor.getLastDonationDate()));
            } else {
                stmt.setNull(8, Types.DATE);
            }

            stmt.setBoolean(9, donor.isAvailability());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating donor: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves a donor by its ID.
     *
     * @param id The donor ID
     * @return The Donor object, or null if not found
     */
    public Donor read(int id) {
        String sql = "SELECT d.id, d.name, d.email, d.phone, d.blood_type_id, d.location_id, " +
                     "d.date_of_birth, d.gender, d.last_donation_date, d.availability, d.created_at, d.updated_at " +
                     "FROM donors d WHERE d.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDonor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading donor: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all donors from the database.
     *
     * @return A list of all Donor objects
     */
    public List<Donor> findAll() {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT d.id, d.name, d.email, d.phone, d.blood_type_id, d.location_id, " +
                     "d.date_of_birth, d.gender, d.last_donation_date, d.availability, d.created_at, d.updated_at " +
                     "FROM donors d ORDER BY d.name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                donors.add(mapResultSetToDonor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all donors: " + e.getMessage());
        }
        return donors;
    }

    /**
     * Finds donors by blood type ID.
     *
     * @param bloodTypeId The blood type ID
     * @return A list of matching Donor objects
     */
    public List<Donor> findByBloodType(int bloodTypeId) {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT d.id, d.name, d.email, d.phone, d.blood_type_id, d.location_id, " +
                     "d.date_of_birth, d.gender, d.last_donation_date, d.availability, d.created_at, d.updated_at " +
                     "FROM donors d WHERE d.blood_type_id = ? AND d.availability = TRUE ORDER BY d.name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bloodTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    donors.add(mapResultSetToDonor(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding donors by blood type: " + e.getMessage());
        }
        return donors;
    }

    /**
     * Finds donors by location ID.
     *
     * @param locationId The location ID
     * @return A list of matching Donor objects
     */
    public List<Donor> findByLocation(int locationId) {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT d.id, d.name, d.email, d.phone, d.blood_type_id, d.location_id, " +
                     "d.date_of_birth, d.gender, d.last_donation_date, d.availability, d.created_at, d.updated_at " +
                     "FROM donors d WHERE d.location_id = ? AND d.availability = TRUE ORDER BY d.name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    donors.add(mapResultSetToDonor(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding donors by location: " + e.getMessage());
        }
        return donors;
    }

    /**
     * Finds donors by both blood type and location.
     *
     * @param bloodTypeId The blood type ID
     * @param locationId The location ID
     * @return A list of matching Donor objects
     */
    public List<Donor> findByBloodTypeAndLocation(int bloodTypeId, int locationId) {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT d.id, d.name, d.email, d.phone, d.blood_type_id, d.location_id, " +
                     "d.date_of_birth, d.gender, d.last_donation_date, d.availability, d.created_at, d.updated_at " +
                     "FROM donors d WHERE d.blood_type_id = ? AND d.location_id = ? AND d.availability = TRUE ORDER BY d.name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bloodTypeId);
            stmt.setInt(2, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    donors.add(mapResultSetToDonor(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding donors by blood type and location: " + e.getMessage());
        }
        return donors;
    }

    /**
     * Updates an existing donor record.
     *
     * @param donor The Donor object with updated values
     * @return true if update was successful, false otherwise
     */
    public boolean update(Donor donor) {
        String sql = "UPDATE donors SET name = ?, email = ?, phone = ?, blood_type_id = ?, location_id = ?, " +
                     "date_of_birth = ?, gender = ?, last_donation_date = ?, availability = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, donor.getName());
            stmt.setString(2, donor.getEmail());
            stmt.setString(3, donor.getPhone());
            stmt.setInt(4, donor.getBloodType().getId());
            stmt.setInt(5, donor.getLocation().getId());
            stmt.setDate(6, java.sql.Date.valueOf(donor.getDateOfBirth()));
            stmt.setString(7, donor.getGender());

            if (donor.getLastDonationDate() != null) {
                stmt.setDate(8, java.sql.Date.valueOf(donor.getLastDonationDate()));
            } else {
                stmt.setNull(8, Types.DATE);
            }

            stmt.setBoolean(9, donor.isAvailability());
            stmt.setInt(10, donor.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating donor: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a donor record by its ID.
     *
     * @param id The donor ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM donors WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting donor: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a Donor object.
     *
     * @param rs The ResultSet
     * @return The mapped Donor object
     * @throws SQLException if column access fails
     */
    private Donor mapResultSetToDonor(ResultSet rs) throws SQLException {
        Donor donor = new Donor();
        donor.setId(rs.getInt("id"));
        donor.setName(rs.getString("name"));
        donor.setEmail(rs.getString("email"));
        donor.setPhone(rs.getString("phone"));

        // Get blood type
        int bloodTypeId = rs.getInt("blood_type_id");
        BloodType bloodType = bloodTypeDAO.read(bloodTypeId);
        donor.setBloodType(bloodType);

        // Get location
        int locationId = rs.getInt("location_id");
        Location location = locationDAO.read(locationId);
        donor.setLocation(location);

        donor.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        donor.setGender(rs.getString("gender"));

        Date lastDonationDate = rs.getDate("last_donation_date");
        if (lastDonationDate != null) {
            donor.setLastDonationDate(lastDonationDate.toLocalDate());
        }

        donor.setAvailability(rs.getBoolean("availability"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            donor.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            donor.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return donor;
    }
}
