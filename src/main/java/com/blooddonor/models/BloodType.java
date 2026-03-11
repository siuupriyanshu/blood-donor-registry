package com.blooddonor.models;

import java.time.LocalDateTime;

/**
 * Model class representing a blood type in the donor registry.
 * Encapsulates blood type information such as type (A+, B-, etc.) and description.
 */
public class BloodType {
    private int id;
    private String type;              // e.g., A+, A-, B+, B-, AB+, AB-, O+, O-
    private String description;
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    public BloodType() {
    }

    /**
     * Constructor with type and description.
     */
    public BloodType(String type, String description) {
        this.type = type;
        this.description = description;
    }

        /**
         * Constructor with id, type, and description.
         */
        public BloodType(int id, String type, String description) {
            this.id = id;
            this.type = type;
            this.description = description;
        }

    /**
     * Constructor with all fields.
     */
    public BloodType(int id, String type, String description, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return type;  // Display only the type when used in UI components
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloodType bloodType = (BloodType) o;
        return id == bloodType.id && (type != null ? type.equals(bloodType.type) : bloodType.type == null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
