package com.blooddonor.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class representing a medical condition associated with a donor.
 * Used to track donor eligibility and health conditions that may affect donation eligibility.
 */
public class MedicalCondition {
    private int id;
    private int donorId;
    private String conditionName;
    private LocalDate diagnosisDate;
    private String notes;
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    public MedicalCondition() {
    }

    /**
     * Constructor with essential fields.
     */
    public MedicalCondition(int donorId, String conditionName, LocalDate diagnosisDate) {
        this.donorId = donorId;
        this.conditionName = conditionName;
        this.diagnosisDate = diagnosisDate;
    }

    /**
     * Constructor with all fields.
     */
    public MedicalCondition(int id, int donorId, String conditionName,
                           LocalDate diagnosisDate, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.donorId = donorId;
        this.conditionName = conditionName;
        this.diagnosisDate = diagnosisDate;
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

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
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
        return "MedicalCondition{" +
                "id=" + id +
                ", donorId=" + donorId +
                ", conditionName='" + conditionName + '\'' +
                ", diagnosisDate=" + diagnosisDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalCondition that = (MedicalCondition) o;
        return id == that.id && donorId == that.donorId;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + donorId;
        return result;
    }
}
