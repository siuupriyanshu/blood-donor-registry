package com.blooddonor.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Utility class for validation operations.
 * Provides methods to validate donor information such as email format, phone format, and age eligibility.
 */
public class ValidationUtils {

    // Email regex pattern
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    // Phone regex pattern - accepts 10-15 digits with optional + and -
    private static final String PHONE_PATTERN = "^[+]?[0-9]{1,3}[-.]?([0-9]{1,4}[-.]?){1,3}[0-9]{1,4}$";
    private static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);

    // Minimum age for blood donation
    private static final int MINIMUM_AGE = 18;

    // Maximum age for blood donation
    private static final int MAXIMUM_AGE = 65;

    /**
     * Validates email format.
     *
     * @param email The email address to validate
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return emailPattern.matcher(email).matches();
    }

    /**
     * Validates phone number format.
     *
     * @param phone The phone number to validate
     * @return true if phone format is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return phonePattern.matcher(phone).matches();
    }

    /**
     * Validates if the date of birth makes the person at least the minimum age.
     *
     * @param dateOfBirth The date of birth
     * @return true if person is at least MINIMUM_AGE years old, false otherwise
     */
    public static boolean isValidAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        int age = Period.between(dateOfBirth, today).getYears();

        return age >= MINIMUM_AGE && age <= MAXIMUM_AGE;
    }

    /**
     * Gets the age of a person based on their date of birth.
     *
     * @param dateOfBirth The date of birth
     * @return The age in years, or -1 if date is invalid
     */
    public static int getAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return -1;
        }
        LocalDate today = LocalDate.now();
        return Period.between(dateOfBirth, today).getYears();
    }

    /**
     * Validates if a donor name is not empty and meets minimum length.
     *
     * @param name The donor name
     * @return true if name is valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.trim().length() <= 150;
    }

    /**
     * Validates if a string is a valid gender.
     *
     * @param gender The gender string
     * @return true if gender is valid (Male, Female, Other), false otherwise
     */
    public static boolean isValidGender(String gender) {
        return gender != null && (gender.equals("Male") || gender.equals("Female") || gender.equals("Other"));
    }

    /**
     * Validates if a blood type is valid.
     *
     * @param bloodType The blood type string
     * @return true if blood type is valid, false otherwise
     */
    public static boolean isValidBloodType(String bloodType) {
        if (bloodType == null || bloodType.isEmpty()) {
            return false;
        }
        return bloodType.matches("^[ABO]+[-+]?$") && 
               (bloodType.equals("O+") || bloodType.equals("O-") ||
                bloodType.equals("A+") || bloodType.equals("A-") ||
                bloodType.equals("B+") || bloodType.equals("B-") ||
                bloodType.equals("AB+") || bloodType.equals("AB-"));
    }

    /**
     * Validates donation volume (must be reasonable - typically 450-500 ml).
     *
     * @param volume The volume in milliliters
     * @return true if volume is valid, false otherwise
     */
    public static boolean isValidDonationVolume(int volume) {
        return volume >= 400 && volume <= 550;
    }
}
