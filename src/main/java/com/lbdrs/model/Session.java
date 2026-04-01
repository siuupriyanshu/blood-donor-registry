package com.lbdrs.model;

/**
 * Holds the currently authenticated user for the duration of the session.
 * Single-instance utility – no DB connection, just in-memory state.
 */
public class Session {

    private static Session instance;
    private User currentUser;

    private Session() {}

    public static synchronized Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public void   setCurrentUser(User user)  { this.currentUser = user; }
    public User   getCurrentUser()           { return currentUser; }
    public boolean isAdmin()                 { return currentUser != null && "Admin".equals(currentUser.getRole()); }
    public boolean isMedicalStaff()          { return currentUser != null && "MedicalStaff".equals(currentUser.getRole()); }
    public boolean canRecordDonation()       { return isAdmin() || isMedicalStaff(); }
    public void   clear()                    { currentUser = null; }
}
