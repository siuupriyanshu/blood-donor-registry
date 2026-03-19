package com.lbdrs.dao;

import com.lbdrs.db.DatabaseConnection;
import com.lbdrs.model.Donor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for the donor table.
 * Implements full CRUD + search + filtering.
 * Uses PreparedStatement throughout to prevent SQL injection.
 * Uses ArrayList for ordered donor lists and HashMap for O(1) ID lookup.
 */
public class DonorRepository {

    // ────────────────────────────────────────────────────────────────────────
    // CREATE
    // ────────────────────────────────────────────────────────────────────────
    public void insertDonor(Donor d) throws SQLException {
        String sql = "INSERT INTO donor " +
                     "(name, age, gender, bloodGroup, address, location, " +
                     " contactNumber, isAvailable, lastDonationDate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1,  d.getName());
            ps.setInt   (2,  d.getAge());
            ps.setString(3,  d.getGender());
            ps.setString(4,  d.getBloodGroup());
            ps.setString(5,  d.getAddress());
            ps.setString(6,  d.getLocation());
            ps.setString(7,  d.getContactNumber());
            ps.setBoolean(8, d.isAvailable());
            ps.setDate  (9,  d.getLastDonationDate() != null
                             ? Date.valueOf(d.getLastDonationDate()) : null);
            ps.executeUpdate();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // READ – all donors  (returned as ArrayList)
    // ────────────────────────────────────────────────────────────────────────
    public List<Donor> getAllDonors() throws SQLException {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT * FROM donor ORDER BY name ASC";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ────────────────────────────────────────────────────────────────────────
    // READ – by ID
    // ────────────────────────────────────────────────────────────────────────
    public Donor getDonorById(int donorId) throws SQLException {
        String sql = "SELECT * FROM donor WHERE donorId = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, donorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // ────────────────────────────────────────────────────────────────────────
    // READ – HashMap for O(1) in-memory lookup (donorId → Donor)
    // ────────────────────────────────────────────────────────────────────────
    public Map<Integer, Donor> getDonorMap() throws SQLException {
        Map<Integer, Donor> map = new HashMap<>();
        for (Donor d : getAllDonors()) map.put(d.getDonorId(), d);
        return map;
    }

    // ────────────────────────────────────────────────────────────────────────
    // READ – keyword search (name, bloodGroup, location)
    // ────────────────────────────────────────────────────────────────────────
    public List<Donor> searchDonors(String keyword) throws SQLException {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT * FROM donor WHERE name LIKE ? OR bloodGroup LIKE ? " +
                     "OR location LIKE ? OR contactNumber LIKE ? ORDER BY name ASC";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            String k = "%" + keyword.trim() + "%";
            ps.setString(1, k); ps.setString(2, k);
            ps.setString(3, k); ps.setString(4, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ────────────────────────────────────────────────────────────────────────
    // READ – filter by blood group (available donors only)
    // ────────────────────────────────────────────────────────────────────────
    public List<Donor> filterByBloodGroup(String bloodGroup) throws SQLException {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT * FROM donor WHERE bloodGroup = ? AND isAvailable = TRUE ORDER BY name ASC";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, bloodGroup.trim());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ────────────────────────────────────────────────────────────────────────
    // READ – filter by location
    // ────────────────────────────────────────────────────────────────────────
    public List<Donor> filterByLocation(String location) throws SQLException {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT * FROM donor WHERE location LIKE ? AND isAvailable = TRUE ORDER BY name ASC";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, "%" + location.trim() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ────────────────────────────────────────────────────────────────────────
    // UPDATE
    // ────────────────────────────────────────────────────────────────────────
    public void updateDonor(Donor d) throws SQLException {
        String sql = "UPDATE donor SET name=?, age=?, gender=?, bloodGroup=?, " +
                     "address=?, location=?, contactNumber=?, isAvailable=?, " +
                     "lastDonationDate=? WHERE donorId=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString (1,  d.getName());
            ps.setInt    (2,  d.getAge());
            ps.setString (3,  d.getGender());
            ps.setString (4,  d.getBloodGroup());
            ps.setString (5,  d.getAddress());
            ps.setString (6,  d.getLocation());
            ps.setString (7,  d.getContactNumber());
            ps.setBoolean(8,  d.isAvailable());
            ps.setDate   (9,  d.getLastDonationDate() != null
                              ? Date.valueOf(d.getLastDonationDate()) : null);
            ps.setInt    (10, d.getDonorId());
            ps.executeUpdate();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // DELETE
    // ────────────────────────────────────────────────────────────────────────
    public void deleteDonor(int donorId) throws SQLException {
        String sql = "DELETE FROM donor WHERE donorId = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, donorId);
            ps.executeUpdate();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Helper – map a ResultSet row to a Donor object
    // ────────────────────────────────────────────────────────────────────────
    private Donor mapRow(ResultSet rs) throws SQLException {
        Date rawDate = rs.getDate("lastDonationDate");
        return new Donor(
                rs.getInt    ("donorId"),
                rs.getString ("name"),
                rs.getInt    ("age"),
                rs.getString ("gender"),
                rs.getString ("bloodGroup"),
                rs.getString ("address"),
                rs.getString ("location"),
                rs.getString ("contactNumber"),
                rawDate != null ? rawDate.toLocalDate() : null,
                rs.getBoolean("isAvailable")
        );
    }
}
