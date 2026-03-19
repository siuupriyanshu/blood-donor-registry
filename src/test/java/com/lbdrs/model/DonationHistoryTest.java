package com.lbdrs.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class DonationHistoryTest {

    @Test
    void historyInfoFormatIsCorrect() {
        DonationHistory h = new DonationHistory(
                1, 10, LocalDate.of(2025, 9, 1), "Completed", "London Blood Bank");
        String info = h.getHistoryInfo();
        assertTrue(info.contains("Completed"),  "Status should be in info string");
        assertTrue(info.contains("London Blood Bank"), "Location should be in info string");
    }

    @Test
    void setDonorNameUpdatesField() {
        DonationHistory h = new DonationHistory();
        h.setDonorName("Alice");
        assertEquals("Alice", h.getDonorName());
    }

    @Test
    void defaultStatusCanBeOverridden() {
        DonationHistory h = new DonationHistory();
        h.setDonationStatus("Deferred");
        assertEquals("Deferred", h.getDonationStatus());
    }
}
