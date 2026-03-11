package com.blooddonor.dao;

import com.blooddonor.models.Location;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Location.
 * Handles CRUD operations for location records in the database.
 */
public class LocationDAO {
    private Connection connection;

    /**
     * Constructor that initializes the connection.
     *
     * @throws SQLException if database connection fails
     */
    public LocationDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Creates a new location record in the database.
     *
     * @param location The Location object to create
     * @return The ID of the created location, or -1 if creation failed
     */
    public int create(Location location) {
        String sql = "INSERT INTO locations (city, state, country) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, location.getCity());
            stmt.setString(2, location.getState());
            stmt.setString(3, location.getCountry());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating location: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves a location by its ID.
     *
     * @param id The location ID
     * @return The Location object, or null if not found
     */
    public Location read(int id) {
        String sql = "SELECT id, city, state, country, created_at FROM locations WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLocation(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading location: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all locations from the database.
     *
     * @return A list of all Location objects
     */
    public List<Location> findAll() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT id, city, state, country, created_at FROM locations ORDER BY city, state";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                locations.add(mapResultSetToLocation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all locations: " + e.getMessage());
        }
        return locations;
    }

    /**
     * Finds a location by city, state, and country.
     *
     * @param city The city name
     * @param state The state name
     * @param country The country name
     * @return The Location object, or null if not found
     */
    public Location findByLocationDetails(String city, String state, String country) {
        String sql = "SELECT id, city, state, country, created_at FROM locations WHERE city = ? AND state = ? AND country = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, city);
            stmt.setString(2, state);
            stmt.setString(3, country);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLocation(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding location: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds locations by city name.
     *
     * @param city The city name (supports partial matching)
     * @return A list of matching Location objects
     */
    public List<Location> findByCity(String city) {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT id, city, state, country, created_at FROM locations WHERE city LIKE ? ORDER BY city";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + city + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    locations.add(mapResultSetToLocation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding locations by city: " + e.getMessage());
        }
        return locations;
    }

    /**
     * Updates an existing location record.
     *
     * @param location The Location object with updated values
     * @return true if update was successful, false otherwise
     */
    public boolean update(Location location) {
        String sql = "UPDATE locations SET city = ?, state = ?, country = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, location.getCity());
            stmt.setString(2, location.getState());
            stmt.setString(3, location.getCountry());
            stmt.setInt(4, location.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating location: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a location record by its ID.
     *
     * @param id The location ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM locations WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting location: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a Location object.
     *
     * @param rs The ResultSet
     * @return The mapped Location object
     * @throws SQLException if column access fails
     */
    private Location mapResultSetToLocation(ResultSet rs) throws SQLException {
        Location location = new Location();
        location.setId(rs.getInt("id"));
        location.setCity(rs.getString("city"));
        location.setState(rs.getString("state"));
        location.setCountry(rs.getString("country"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            location.setCreatedAt(createdAt.toLocalDateTime());
        }
        return location;
    }
}
