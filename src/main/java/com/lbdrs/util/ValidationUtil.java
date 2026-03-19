package com.lbdrs.util;

import java.util.regex.Pattern;

/**
 * Centralised input validation utility.
 * Keeps validation logic out of controllers and models.
 */
public final class ValidationUtil {

    private static final Pattern PHONE   = Pattern.compile("^[0-9+\\-\\s]{7,20}$");
    private static final String[] BLOOD_GROUPS = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};

    private ValidationUtil() {}

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidAge(int age) {
        return age >= 18 && age <= 65;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE.matcher(phone.trim()).matches();
    }

    public static boolean isValidBloodGroup(String bg) {
        if (bg == null) return false;
        for (String g : BLOOD_GROUPS) if (g.equalsIgnoreCase(bg.trim())) return true;
        return false;
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.trim().length() >= 3;
    }

    public static boolean isValidPassword(String password) {
        // Minimum 6 characters
        return password != null && password.length() >= 6;
    }

    /**
     * Validates all mandatory donor fields and returns an error message,
     * or null if everything is valid.
     */
    public static String validateDonor(String name, String ageStr,
                                       String bloodGroup, String contact) {
        if (isBlank(name))        return "Name is required.";
        if (isBlank(ageStr))      return "Age is required.";
        int age;
        try { age = Integer.parseInt(ageStr.trim()); }
        catch (NumberFormatException e) { return "Age must be a number."; }
        if (!isValidAge(age))     return "Age must be between 18 and 65.";
        if (!isValidBloodGroup(bloodGroup)) return "Invalid blood group.";
        if (!isValidPhone(contact))         return "Invalid contact number.";
        return null; // all OK
    }
}
