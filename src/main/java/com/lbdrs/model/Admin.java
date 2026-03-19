package com.lbdrs.model;

/**
 * Admin user – extends User.
 * Has full CRUD access to donor records.
 */
public class Admin extends User {

    private String adminRole;

    public Admin() { super(); }

    public Admin(int userId, String userName, String password, String adminRole) {
        super(userId, userName, password, "Admin");
        this.adminRole = adminRole;
    }

    public String getAdminRole() { return adminRole; }
    public void   setAdminRole(String adminRole) { this.adminRole = adminRole; }

    @Override
    public boolean login(String username, String password) {
        // Actual credential verification is delegated to LoginController + BCrypt
        return this.getUserName().equals(username);
    }

    @Override
    public void logout() {
        System.out.println("[Admin] " + getUserName() + " has logged out.");
    }
}
