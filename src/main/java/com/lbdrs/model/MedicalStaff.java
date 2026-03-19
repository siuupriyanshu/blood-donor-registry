package com.lbdrs.model;

/**
 * Medical Staff user – extends User.
 * Can search donors and record donations; cannot modify donor records.
 */
public class MedicalStaff extends User {

    private String staffRole;

    public MedicalStaff() { super(); }

    public MedicalStaff(int userId, String userName, String password, String staffRole) {
        super(userId, userName, password, "MedicalStaff");
        this.staffRole = staffRole;
    }

    public String getStaffRole() { return staffRole; }
    public void   setStaffRole(String staffRole) { this.staffRole = staffRole; }

    @Override
    public boolean login(String username, String password) {
        return this.getUserName().equals(username);
    }

    @Override
    public void logout() {
        System.out.println("[MedicalStaff] " + getUserName() + " has logged out.");
    }
}
