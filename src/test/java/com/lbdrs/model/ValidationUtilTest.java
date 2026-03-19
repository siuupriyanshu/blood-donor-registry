package com.lbdrs.model;

import com.lbdrs.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil – no DB required.
 */
class ValidationUtilTest {

    // ── Age ───────────────────────────────────────────────────────────────────
    @Test void ageOf18IsValid()  { assertTrue(ValidationUtil.isValidAge(18)); }
    @Test void ageOf65IsValid()  { assertTrue(ValidationUtil.isValidAge(65)); }
    @Test void ageOf17IsInvalid(){ assertFalse(ValidationUtil.isValidAge(17)); }
    @Test void ageOf66IsInvalid(){ assertFalse(ValidationUtil.isValidAge(66)); }

    // ── Blood group ───────────────────────────────────────────────────────────
    @Test void validBloodGroupAPos()  { assertTrue(ValidationUtil.isValidBloodGroup("A+"));  }
    @Test void validBloodGroupONeg()  { assertTrue(ValidationUtil.isValidBloodGroup("O-"));  }
    @Test void validBloodGroupABPos() { assertTrue(ValidationUtil.isValidBloodGroup("AB+")); }
    @Test void invalidBloodGroupXX()  { assertFalse(ValidationUtil.isValidBloodGroup("XX")); }
    @Test void nullBloodGroupInvalid(){ assertFalse(ValidationUtil.isValidBloodGroup(null)); }

    // ── Phone ─────────────────────────────────────────────────────────────────
    @Test void validPhone()          { assertTrue(ValidationUtil.isValidPhone("07111222333")); }
    @Test void validPhoneWithPlus()  { assertTrue(ValidationUtil.isValidPhone("+44 7111 222 333")); }
    @Test void invalidPhoneTooShort(){ assertFalse(ValidationUtil.isValidPhone("123")); }
    @Test void nullPhoneInvalid()    { assertFalse(ValidationUtil.isValidPhone(null)); }

    // ── Blank check ───────────────────────────────────────────────────────────
    @Test void nullIsBlank()        { assertTrue(ValidationUtil.isBlank(null)); }
    @Test void emptyStringIsBlank() { assertTrue(ValidationUtil.isBlank("  ")); }
    @Test void nonEmptyIsNotBlank() { assertFalse(ValidationUtil.isBlank("hello")); }

    // ── validateDonor integration ─────────────────────────────────────────────
    @Test void validDonorReturnsNull() {
        assertNull(ValidationUtil.validateDonor("Alice", "25", "A+", "07111000000"));
    }

    @Test void missingNameReturnsError() {
        assertNotNull(ValidationUtil.validateDonor("", "25", "A+", "07111000000"));
    }

    @Test void nonNumericAgeReturnsError() {
        assertNotNull(ValidationUtil.validateDonor("Alice", "abc", "A+", "07111000000"));
    }

    @Test void underageReturnsError() {
        assertNotNull(ValidationUtil.validateDonor("Alice", "16", "A+", "07111000000"));
    }

    @Test void badBloodGroupReturnsError() {
        assertNotNull(ValidationUtil.validateDonor("Alice", "25", "Z+", "07111000000"));
    }
}
