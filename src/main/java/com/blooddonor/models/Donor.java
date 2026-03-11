package com.blooddonor.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class representing a blood donor in the registry.
 * Encapsulates donor information including personal details, blood type, location, and donation history.
 */
public class Donor {
    private int id;
    private String name;
    private String email;
    private String phone;
    private BloodType bloodType;
    private Location location;
    private LocalDate dateOfBirth;
    private String gender;              // Male, Female, Other
    private LocalDate lastDonationDate;
    private boolean availability;       // Can donate or not
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    public Donor() {
    }

    /**
     * Constructor with essential fields.
     */
    public Donor(String name, String email, String phone, BloodType bloodType, Location location,
                 LocalDate dateOfBirth, String gender, boolean availability) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bloodType = bloodType;
        this.location = location;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.availability = availability;
    }

    /**
     * Constructor with all fields.
     */
    public Donor(int id, String name, String email, String phone, BloodType bloodType,
                 Location location, LocalDate dateOfBirth, String gender,
                 LocalDate lastDonationDate, boolean availability,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bloodType = bloodType;
        this.location = location;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.lastDonationDate = lastDonationDate;
        this.availability = availability;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Donor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", bloodType=" + bloodType +
                ", location=" + location +
                ", gender='" + gender + '\'' +
                ", availability=" + availability +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donor donor = (Donor) o;
        return id == donor.id && (email != null ? email.equals(donor.email) : donor.email == null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
