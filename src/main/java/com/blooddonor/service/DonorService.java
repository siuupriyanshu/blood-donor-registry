package com.blooddonor.service;

import com.blooddonor.dao.*;
import com.blooddonor.models.*;
import com.blooddonor.util.DateUtils;
import com.blooddonor.util.ValidationUtils;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for Donor operations.
 * Handles business logic related to donor management, validation, and eligibility checks.
 */
public class DonorService {
    private DonorDAO donorDAO;
    private DonationRecordDAO donationRecordDAO;
    private MedicalConditionDAO medicalConditionDAO;

    /**
     * Constructor that initializes the DAOs.
     *
     * @throws SQLException if database initialization fails
     */
    public DonorService() throws SQLException {
        this.donorDAO = new DonorDAO();
        this.donationRecordDAO = new DonationRecordDAO();
        this.medicalConditionDAO = new MedicalConditionDAO();
    }

    /**
     * Creates a new donor with validation.
     *
     * @param donor The donor to create
     * @return The ID of the created donor, or -1 if creation failed
     * @throws IllegalArgumentException if donor data is invalid
     */
    public int createDonor(Donor donor) throws IllegalArgumentException {
        validateDonor(donor);
        return donorDAO.create(donor);
    }

    /**
     * Retrieves a donor by ID.
     *
     * @param id The donor ID
     * @return The Donor object, or null if not found
     */
    public Donor getDonorById(int id) {
        return donorDAO.read(id);
    }

    /**
     * Retrieves all donors.
     *
     * @return A list of all donors
     */
    public List<Donor> getAllDonors() {
        return donorDAO.findAll();
    }

    /**
     * Updates an existing donor with validation.
     *
     * @param donor The donor with updated information
     * @return true if update was successful, false otherwise
     * @throws IllegalArgumentException if donor data is invalid
     */
    public boolean updateDonor(Donor donor) throws IllegalArgumentException {
        validateDonor(donor);
        return donorDAO.update(donor);
    }

    /**
     * Deletes a donor.
     *
     * @param id The donor ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteDonor(int id) {
        return donorDAO.delete(id);
    }

    /**
     * Validates all donor information.
     *
     * @param donor The donor to validate
     * @throws IllegalArgumentException if any validation fails
     */
    public void validateDonor(Donor donor) throws IllegalArgumentException {
        if (donor == null) {
            throw new IllegalArgumentException("Donor cannot be null");
        }

        if (!ValidationUtils.isValidName(donor.getName())) {
            throw new IllegalArgumentException("Donor name must be between 2 and 150 characters");
        }

        if (donor.getEmail() != null && !donor.getEmail().isEmpty()) {
            if (!ValidationUtils.isValidEmail(donor.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }

        if (donor.getPhone() != null && !donor.getPhone().isEmpty()) {
            if (!ValidationUtils.isValidPhone(donor.getPhone())) {
                throw new IllegalArgumentException("Invalid phone format");
            }
        }

        if (!ValidationUtils.isValidAge(donor.getDateOfBirth())) {
            throw new IllegalArgumentException("Donor must be between 18 and 65 years old");
        }

        if (!ValidationUtils.isValidGender(donor.getGender())) {
            throw new IllegalArgumentException("Invalid gender. Must be Male, Female, or Other");
        }

        if (donor.getBloodType() == null || donor.getBloodType().getId() <= 0) {
            throw new IllegalArgumentException("Blood type must be selected");
        }

        if (donor.getLocation() == null || donor.getLocation().getId() <= 0) {
            throw new IllegalArgumentException("Location must be selected");
        }
    }

    /**
     * Checks if a donor is eligible to donate based on time since last donation.
     *
     * @param donorId The donor ID
     * @return true if donor is eligible to donate, false otherwise
     */
    public boolean isDonorEligibleToDonate(int donorId) {
        Donor donor = donorDAO.read(donorId);
        if (donor == null) {
            return false;
        }

        // Check medical conditions
        if (hasCriticalMedicalConditions(donorId)) {
            return false;
        }

        // Check if availability flag is set
        if (!donor.isAvailability()) {
            return false;
        }

        // Check time since last donation
        return DateUtils.isEligibleToDonate(donor.getLastDonationDate());
    }

    /**
     * Gets the number of days until a donor becomes eligible to donate again.
     *
     * @param donorId The donor ID
     * @return The number of days until eligible, or 0 if already eligible
     */
    public long getDaysUntilDonationEligible(int donorId) {
        Donor donor = donorDAO.read(donorId);
        if (donor == null) {
            return -1;
        }
        return DateUtils.getDaysUntilEligible(donor.getLastDonationDate());
    }

    /**
     * Records a new donation for a donor and updates last donation date.
     *
     * @param donorId The donor ID
     * @param donationDate The date of donation
     * @param volumeCollected The volume collected in ml
     * @param status The status of the donation
     * @return true if donation record was created and donor updated, false otherwise
     */
    public boolean recordDonation(int donorId, LocalDate donationDate, int volumeCollected, String status) {
        // Create donation record
        DonationRecord record = new DonationRecord(donorId, donationDate, volumeCollected, status);
        int recordId = donationRecordDAO.create(record);

        if (recordId <= 0) {
            return false;
        }

        // Update donor's last donation date if status is Completed
        if ("Completed".equals(status)) {
            Donor donor = donorDAO.read(donorId);
            if (donor != null) {
                donor.setLastDonationDate(donationDate);
                return donorDAO.update(donor);
            }
        }

        return true;
    }

    /**
     * Gets all donation records for a specific donor.
     *
     * @param donorId The donor ID
     * @return A list of donation records for the donor
     */
    public List<DonationRecord> getDonationHistory(int donorId) {
        return donationRecordDAO.findByDonorId(donorId);
    }

    /**
     * Adds a medical condition to a donor.
     *
     * @param donorId The donor ID
     * @param conditionName The name of the condition
     * @param diagnosisDate The date of diagnosis
     * @return The ID of the created condition, or -1 if failed
     */
    public int addMedicalCondition(int donorId, String conditionName, LocalDate diagnosisDate) {
        MedicalCondition condition = new MedicalCondition(donorId, conditionName, diagnosisDate);
        return medicalConditionDAO.create(condition);
    }

    /**
     * Gets all medical conditions for a donor.
     *
     * @param donorId The donor ID
     * @return A list of medical conditions
     */
    public List<MedicalCondition> getMedicalConditions(int donorId) {
        return medicalConditionDAO.findByDonorId(donorId);
    }

    /**
     * Checks if a donor has any critical medical conditions that prevent donation.
     *
     * @param donorId The donor ID
     * @return true if donor has critical conditions, false otherwise
     */
    private boolean hasCriticalMedicalConditions(int donorId) {
        List<MedicalCondition> conditions = medicalConditionDAO.findByDonorId(donorId);

        // List of conditions that prevent donation
        String[] criticalConditions = {
            "Active Infection", "Cancer", "Heart Disease", "Kidney Disease",
            "Hepatitis B", "Hepatitis C", "HIV/AIDS", "Tuberculosis"
        };

        for (MedicalCondition condition : conditions) {
            for (String critical : criticalConditions) {
                if (condition.getConditionName().contains(critical)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets total number of donations by a donor.
     *
     * @param donorId The donor ID
     * @return The number of donations
     */
    public int getTotalDonations(int donorId) {
        List<DonationRecord> records = donationRecordDAO.findByDonorId(donorId);
        return (int) records.stream().filter(r -> "Completed".equals(r.getStatus())).count();
    }

    /**
     * Gets total volume donated by a donor in milliliters.
     *
     * @param donorId The donor ID
     * @return The total volume in ml
     */
    public int getTotalVolumeDonated(int donorId) {
        List<DonationRecord> records = donationRecordDAO.findByDonorId(donorId);
        return (int) records.stream()
                .filter(r -> "Completed".equals(r.getStatus()))
                .mapToLong(DonationRecord::getVolumeCollected)
                .sum();
    }
}
