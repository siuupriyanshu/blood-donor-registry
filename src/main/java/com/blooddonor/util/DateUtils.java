package com.blooddonor.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date-related operations.
 * Provides methods for date formatting, donation eligibility calculation, and date comparisons.
 */
public class DateUtils {

    // Standard donation eligibility interval (56 days for whole blood donation)
    private static final long DONATION_INTERVAL_DAYS = 56;

    // Date formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Formats a LocalDate to a readable string format (MM/dd/yyyy).
     *
     * @param date The date to format
     * @return Formatted date string, or "N/A" if date is null
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Parses a string date in MM/dd/yyyy format to LocalDate.
     *
     * @param dateString The date string to parse
     * @return The parsed LocalDate, or null if parsing fails
     */
    public static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Checks if a donor is eligible to donate based on their last donation date.
     * A donor is eligible if more than 56 days have passed since their last donation.
     *
     * @param lastDonationDate The last date the donor donated (can be null for new donors)
     * @return true if donor is eligible to donate, false otherwise
     */
    public static boolean isEligibleToDonate(LocalDate lastDonationDate) {
        // New donors (no previous donation) are always eligible
        if (lastDonationDate == null) {
            return true;
        }

        LocalDate today = LocalDate.now();
        long daysSinceLastDonation = ChronoUnit.DAYS.between(lastDonationDate, today);

        return daysSinceLastDonation >= DONATION_INTERVAL_DAYS;
    }

    /**
     * Calculates the number of days since the last donation.
     *
     * @param lastDonationDate The last donation date
     * @return The number of days since last donation, or -1 if date is null
     */
    public static long getDaysSinceLastDonation(LocalDate lastDonationDate) {
        if (lastDonationDate == null) {
            return -1;
        }

        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(lastDonationDate, today);
    }

    /**
     * Calculates the eligible donation date after the specified last donation date.
     *
     * @param lastDonationDate The last donation date
     * @return The eligible donation date (56 days after last donation)
     */
    public static LocalDate getEligibleDonationDate(LocalDate lastDonationDate) {
        if (lastDonationDate == null) {
            return LocalDate.now();
        }
        return lastDonationDate.plusDays(DONATION_INTERVAL_DAYS);
    }

    /**
     * Returns the number of days until a donor becomes eligible to donate again.
     *
     * @param lastDonationDate The last donation date
     * @return The number of days until eligible, or 0 if already eligible
     */
    public static long getDaysUntilEligible(LocalDate lastDonationDate) {
        if (lastDonationDate == null) {
            return 0;
        }

        LocalDate eligibleDate = getEligibleDonationDate(lastDonationDate);
        LocalDate today = LocalDate.now();

        if (today.isAfter(eligibleDate)) {
            return 0;
        }

        return ChronoUnit.DAYS.between(today, eligibleDate);
    }

    /**
     * Checks if a date is in the past.
     *
     * @param date The date to check
     * @return true if date is before today, false otherwise
     */
    public static boolean isDateInPast(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(LocalDate.now());
    }

    /**
     * Checks if a date is today or in the future.
     *
     * @param date The date to check
     * @return true if date is today or after today, false otherwise
     */
    public static boolean isDateInFutureOrToday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(LocalDate.now());
    }

    /**
     * Gets the donation interval in days.
     *
     * @return The number of days required between donations
     */
    public static long getDonationIntervalDays() {
        return DONATION_INTERVAL_DAYS;
    }
}
