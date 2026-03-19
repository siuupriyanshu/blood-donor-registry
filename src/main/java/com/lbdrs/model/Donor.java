package com.lbdrs.model;

import java.time.LocalDate;

/**
 * Represents a blood donor.
 * All fields are private – encapsulation enforced via getters/setters.
 */
public class Donor {

    private int       donorId;
    private String    name;
    private int       age;
    private String    gender;
    private String    bloodGroup;
    private String    address;
    private String    location;
    private String    contactNumber;
    private LocalDate lastDonationDate;
    private boolean   isAvailable;

    // ── Constructors ────────────────────────────────────────────────────────
    public Donor() {}

    public Donor(int donorId, String name, int age, String gender,
                 String bloodGroup, String address, String location,
                 String contactNumber, LocalDate lastDonationDate, boolean isAvailable) {
        this.donorId          = donorId;
        this.name             = name;
        this.age              = age;
        this.gender           = gender;
        this.bloodGroup       = bloodGroup;
        this.address          = address;
        this.location         = location;
        this.contactNumber    = contactNumber;
        this.lastDonationDate = lastDonationDate;
        this.isAvailable      = isAvailable;
    }

    // ── Business methods ────────────────────────────────────────────────────
    /**
     * Returns a short summary string for display in tables.
     */
    public String getDonorDetails() {
        return name + " | " + bloodGroup + " | " + location;
    }

    /**
     * A donor is eligible if they are available AND their last donation
     * was at least 56 days ago (standard NHS guideline).
     */
    public boolean isEligible() {
        if (!isAvailable) return false;
        if (lastDonationDate == null) return true;
        return lastDonationDate.plusDays(56).isBefore(LocalDate.now());
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public int       getDonorId()          { return donorId; }
    public String    getName()             { return name; }
    public int       getAge()              { return age; }
    public String    getGender()           { return gender; }
    public String    getBloodGroup()       { return bloodGroup; }
    public String    getAddress()          { return address; }
    public String    getLocation()         { return location; }
    public String    getContactNumber()    { return contactNumber; }
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public boolean   isAvailable()         { return isAvailable; }

    public void setDonorId(int id)                      { this.donorId = id; }
    public void setName(String name)                    { this.name = name; }
    public void setAge(int age)                         { this.age = age; }
    public void setGender(String gender)                { this.gender = gender; }
    public void setBloodGroup(String bloodGroup)        { this.bloodGroup = bloodGroup; }
    public void setAddress(String address)              { this.address = address; }
    public void setLocation(String location)            { this.location = location; }
    public void setContactNumber(String c)              { this.contactNumber = c; }
    public void setLastDonationDate(LocalDate d)        { this.lastDonationDate = d; }
    public void setAvailable(boolean available)         { this.isAvailable = available; }

    @Override
    public String toString() {
        return "Donor{id=" + donorId + ", name='" + name + "', bloodGroup='" + bloodGroup + "'}";
    }
}
