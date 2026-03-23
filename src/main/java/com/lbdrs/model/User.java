package com.lbdrs.model;

/**
 * Abstract base class for all system users.
 * Demonstrates: Abstraction, Inheritance, Encapsulation.
 */
public abstract class User {

    private int    userId;
    private String userName;
    private String password;   
    private String role;

    protected User() {}

    protected User(int userId, String userName, String password, String role) {
        this.userId   = userId;
        this.userName = userName;
        this.password = password;
        this.role     = role;
    }

    // ── Encapsulated accessors ──────────────────────────────────────────────
    public int    getUserId()   { return userId; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    public void setUserId(int userId)       { this.userId = userId; }
    public void setUserName(String name)    { this.userName = name; }
    public void setPassword(String pass)    { this.password = pass; }
    public void setRole(String role)        { this.role = role; }

    /** Subclasses implement login validation logic. */
    public abstract boolean login(String username, String password);

    /** Subclasses implement logout / cleanup logic. */
    public abstract void logout();

    @Override
    public String toString() {
        return "User{userId=" + userId + ", userName='" + userName + "', role='" + role + "'}";
    }
}
