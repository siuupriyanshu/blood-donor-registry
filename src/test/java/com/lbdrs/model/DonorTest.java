package com.lbdrs.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Unit tests for the Donor model.
 * No DB connection required – pure business logic.
 */
class DonorTest {

    private Donor makeDonor(boolean available, LocalDate lastDonation) {
        return new Donor(1, "Alice Johnson", 28, "Female", "A+",
                         "12 Oak Street", "London", "07111111101",
                         lastDonation, available);
    }

    // ── Availability ─────────────────────────────────────────────────────────
    @Test
    void newDonorIsAvailableByDefault() {
        Donor d = makeDonor(true, null);
        assertTrue(d.isAvailable(), "Newly registered donor should be marked available");
    }

    @Test
    void unavailableDonorIsNotEligible() {
        Donor d = makeDonor(false, LocalDate.now().minusDays(100));
        assertFalse(d.isEligible(), "Unavailable donor must not be eligible");
    }

    @Test
    void donorEligibleAfter56Days() {
        Donor d = makeDonor(true, LocalDate.now().minusDays(57));
        assertTrue(d.isEligible(), "Donor who donated 57 days ago should be eligible");
    }

    @Test
    void donorNotEligibleWithin56Days() {
        Donor d = makeDonor(true, LocalDate.now().minusDays(30));
        assertFalse(d.isEligible(), "Donor who donated 30 days ago must NOT be eligible");
    }

    @Test
    void donorWithNullLastDonationDateIsEligible() {
        Donor d = makeDonor(true, null);
        assertTrue(d.isEligible(), "First-time donor (null date) should be eligible");
    }

    // ── getDonorDetails ───────────────────────────────────────────────────────
    @Test
    void donorDetailsFormatIsCorrect() {
        Donor d = makeDonor(true, null);
        assertEquals("Alice Johnson | A+ | London", d.getDonorDetails());
    }

    // ── Setters / encapsulation ───────────────────────────────────────────────
    @Test
    void setAvailableUpdatesState() {
        Donor d = makeDonor(true, null);
        d.setAvailable(false);
        assertFalse(d.isAvailable());
    }

    @Test
    void setBloodGroupUpdatesValue() {
        Donor d = makeDonor(true, null);
        d.setBloodGroup("O-");
        assertEquals("O-", d.getBloodGroup());
    }

    // ── Age boundaries ────────────────────────────────────────────────────────
    @Test
    void donorAgeStoredCorrectly() {
        Donor d = new Donor(2, "Bob", 18, "Male", "O+",
                            "Addr", "City", "07000000000", null, true);
        assertEquals(18, d.getAge());
    }
}
