package com.lbdrs.model;

import java.time.LocalDate;

/**
 * Represents a single blood donation record.
 */
public class DonationHistory {

    private int       historyId;
    private int       donorId;
    private String    donorName;   // joined from donor table for display
    private LocalDate donationDate;
    private String    donationStatus;
    private String    location;

    // ── Constructors ────────────────────────────────────────────────────────
    public DonationHistory() {}

    public DonationHistory(int historyId, int donorId, LocalDate donationDate,
                           String donationStatus, String location) {
        this.historyId      = historyId;
        this.donorId        = donorId;
        this.donationDate   = donationDate;
        this.donationStatus = donationStatus;
        this.location       = location;
    }

    // ── Business method ─────────────────────────────────────────────────────
    public void addHistory() {
        System.out.println("Donation recorded: " + donorId + " on " + donationDate);
    }

    public String getHistoryInfo() {
        return donationDate + " | " + donationStatus + " | " + location;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public int       getHistoryId()      { return historyId; }
    public int       getDonorId()        { return donorId; }
    public String    getDonorName()      { return donorName; }
    public LocalDate getDonationDate()   { return donationDate; }
    public String    getDonationStatus() { return donationStatus; }
    public String    getLocation()       { return location; }

    public void setHistoryId(int id)              { this.historyId = id; }
    public void setDonorId(int donorId)           { this.donorId = donorId; }
    public void setDonorName(String name)         { this.donorName = name; }
    public void setDonationDate(LocalDate d)      { this.donationDate = d; }
    public void setDonationStatus(String status)  { this.donationStatus = status; }
    public void setLocation(String location)      { this.location = location; }

    @Override
    public String toString() {
        return "DonationHistory{historyId=" + historyId + ", donorId=" + donorId
                + ", date=" + donationDate + ", status='" + donationStatus + "'}";
    }
}
