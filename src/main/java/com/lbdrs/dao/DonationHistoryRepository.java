package com.lbdrs.dao;

import com.lbdrs.db.DatabaseConnection;
import com.lbdrs.model.DonationHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the donation_history table.
 */
public class DonationHistoryRepository {

    // ────────────────────────────────────────────────────────────────────────
    // INSERT
    // ────────────────────────────────────────────────────────────────────────
    public void addHistory(DonationHistory h) throws SQLException {
        String sql = "INSERT INTO donation_history (donorId, donationDate, donationStatus, location) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt   (1, h.getDonorId());
            ps.setDate  (2, Date.valueOf(h.getDonationDate()));
            ps.setString(3, h.getDonationStatus() != null ? h.getDonationStatus() : "Completed");
            ps.setString(4, h.getLocation());
            ps.executeUpdate();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // SELECT – all history for a donor (most recent first)
    // ────────────────────────────────────────────────────────────────────────
    public List<DonationHistory> getHistoryByDonor(int donorId) throws SQLException {
        List<DonationHistory> list = new ArrayList<>();
        String sql = "SELECT dh.*, d.name AS donorName FROM donation_history dh " +
                     "JOIN donor d ON dh.donorId = d.donorId " +
                     "WHERE dh.donorId = ? ORDER BY dh.donationDate DESC";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, donorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ────────────────────────────────────────────────────────────────────────
    // SELECT – all history (admin overview)
    // ────────────────────────────────────────────────────────────────────────
    public List<DonationHistory> getAllHistory() throws SQLException {
        List<DonationHistory> list = new ArrayList<>();
        String sql = "SELECT dh.*, d.name AS donorName FROM donation_history dh " +
                     "JOIN donor d ON dh.donorId = d.donorId " +
                     "ORDER BY dh.donationDate DESC";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ────────────────────────────────────────────────────────────────────────
    // DELETE
    // ────────────────────────────────────────────────────────────────────────
    public void deleteHistory(int historyId) throws SQLException {
        String sql = "DELETE FROM donation_history WHERE historyId = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, historyId);
            ps.executeUpdate();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Helper
    // ────────────────────────────────────────────────────────────────────────
    private DonationHistory mapRow(ResultSet rs) throws SQLException {
        DonationHistory h = new DonationHistory(
                rs.getInt    ("historyId"),
                rs.getInt    ("donorId"),
                rs.getDate   ("donationDate").toLocalDate(),
                rs.getString ("donationStatus"),
                rs.getString ("location")
        );
        h.setDonorName(rs.getString("donorName"));
        return h;
    }
}
