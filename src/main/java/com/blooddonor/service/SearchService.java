package com.blooddonor.service;

import com.blooddonor.dao.DonorDAO;
import com.blooddonor.dao.BloodTypeDAO;
import com.blooddonor.dao.LocationDAO;
import com.blooddonor.models.Donor;
import com.blooddonor.models.BloodType;
import com.blooddonor.models.Location;
import com.blooddonor.util.DateUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for search and filtering operations.
 * Handles complex queries and filtering for donors by blood type, location, and availability.
 */
public class SearchService {
    private DonorDAO donorDAO;
    private BloodTypeDAO bloodTypeDAO;
    private LocationDAO locationDAO;

    /**
     * Constructor that initializes the DAOs.
     *
     * @throws SQLException if database initialization fails
     */
    public SearchService() throws SQLException {
        this.donorDAO = new DonorDAO();
        this.bloodTypeDAO = new BloodTypeDAO();
        this.locationDAO = new LocationDAO();
    }

    /**
     * Searches for donors by blood type.
     *
     * @param bloodTypeId The blood type ID
     * @return A list of available donors with the specified blood type
     */
    public List<Donor> searchByBloodType(int bloodTypeId) {
        return donorDAO.findByBloodType(bloodTypeId);
    }

    /**
     * Searches for donors by location.
     *
     * @param locationId The location ID
     * @return A list of available donors from the specified location
     */
    public List<Donor> searchByLocation(int locationId) {
        return donorDAO.findByLocation(locationId);
    }

    /**
     * Searches for donors by both blood type and location.
     *
     * @param bloodTypeId The blood type ID
     * @param locationId The location ID
     * @return A list of available donors matching both criteria
     */
    public List<Donor> searchByBloodTypeAndLocation(int bloodTypeId, int locationId) {
        return donorDAO.findByBloodTypeAndLocation(bloodTypeId, locationId);
    }

    /**
     * Searches for donors eligible to donate immediately (availability and time-based eligibility).
     *
     * @param bloodTypeId Optional blood type ID (pass -1 for any blood type)
     * @param locationId Optional location ID (pass -1 for any location)
     * @return A list of donors who are immediately eligible to donate
     */
    public List<Donor> searchEligibleDonors(int bloodTypeId, int locationId) {
        List<Donor> donors = new ArrayList<>();

        // Get base list of available donors
        if (bloodTypeId > 0 && locationId > 0) {
            donors = searchByBloodTypeAndLocation(bloodTypeId, locationId);
        } else if (bloodTypeId > 0) {
            donors = searchByBloodType(bloodTypeId);
        } else if (locationId > 0) {
            donors = searchByLocation(locationId);
        } else {
            donors = donorDAO.findAll();
        }

        // Filter by eligibility (time since last donation)
        return donors.stream()
                .filter(d -> d.isAvailability() && DateUtils.isEligibleToDonate(d.getLastDonationDate()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all blood types.
     *
     * @return A list of all blood types
     */
        public List<Donor> getAllDonors() {
            return donorDAO.findAll();
        }

        /**
         * Gets all blood types.
         *
         * @return A list of all blood types
         */
    public List<BloodType> getAllBloodTypes() {
        return bloodTypeDAO.findAll();
    }

    /**
     * Gets all locations.
     *
     * @return A list of all locations
     */
    public List<Location> getAllLocations() {
        return locationDAO.findAll();
    }

    /**
     * Gets a blood type by ID.
     *
     * @param id The blood type ID
     * @return The BloodType object, or null if not found
     */
    public BloodType getBloodTypeById(int id) {
        return bloodTypeDAO.read(id);
    }

    /**
     * Gets a location by ID.
     *
     * @param id The location ID
     * @return The Location object, or null if not found
     */
    public Location getLocationById(int id) {
        return locationDAO.read(id);
    }

    /**
     * Generates a report of donors by blood type.
     *
     * @return A list of blood types with donor counts
     */
    public List<BloodTypeReport> getDonorCountByBloodType() {
        List<BloodType> bloodTypes = getAllBloodTypes();
        List<BloodTypeReport> reports = new ArrayList<>();

        for (BloodType bloodType : bloodTypes) {
            int count = donorDAO.findByBloodType(bloodType.getId()).size();
            reports.add(new BloodTypeReport(bloodType.getType(), count));
        }

        return reports;
    }

    /**
     * Generates a report of donors by location.
     *
     * @return A list of locations with donor counts
     */
    public List<LocationReport> getDonorCountByLocation() {
        List<Location> locations = getAllLocations();
        List<LocationReport> reports = new ArrayList<>();

        for (Location location : locations) {
            int count = donorDAO.findByLocation(location.getId()).size();
            reports.add(new LocationReport(location.toString(), count));
        }

        return reports;
    }

    /**
     * Inner class for blood type report data.
     */
    public static class BloodTypeReport {
        public String bloodType;
        public int donorCount;

        public BloodTypeReport(String bloodType, int donorCount) {
            this.bloodType = bloodType;
            this.donorCount = donorCount;
        }

        @Override
        public String toString() {
            return bloodType + ": " + donorCount + " donors";
        }
    }

    /**
     * Inner class for location report data.
     */
    public static class LocationReport {
        public String location;
        public int donorCount;

        public LocationReport(String location, int donorCount) {
            this.location = location;
            this.donorCount = donorCount;
        }

        @Override
        public String toString() {
            return location + ": " + donorCount + " donors";
        }
    }
}
