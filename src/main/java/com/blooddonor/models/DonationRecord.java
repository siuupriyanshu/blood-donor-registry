package com.blooddonor.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class representing a donation record.
 * Encapsulates information about a specific donation by a donor.
 */
public class DonationRecord {
    private int id;
    private int donorId;
    private LocalDate donationDate;
    private int volumeCollected;       // in milliliters
    private String status;              // Completed, Pending, Failed, Cancelled
    private String notes;
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    public DonationRecord() {
    }

    /**
     * Constructor with essential fields.
     */
    public DonationRecord(int donorId, LocalDate donationDate, int volumeCollected, String status) {
        this.donorId = donorId;
        this.donationDate = donationDate;
        this.volumeCollected = volumeCollected;
        this.status = status;
    }

    /**
     * Constructor with all fields.
     */
    public DonationRecord(int id, int donorId, LocalDate donationDate, int volumeCollected,
                         String status, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.donorId = donorId;
        this.donationDate = donationDate;
        this.volumeCollected = volumeCollected;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public int getVolumeCollected() {
        return volumeCollected;
    }

    public void setVolumeCollected(int volumeCollected) {
        this.volumeCollected = volumeCollected;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DonationRecord{" +
                "id=" + id +
                ", donorId=" + donorId +
                ", donationDate=" + donationDate +
                ", volumeCollected=" + volumeCollected +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DonationRecord that = (DonationRecord) o;
        return id == that.id && donorId == that.donorId;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + donorId;
        return result;
    }
}
