package com.nhom14.model;

public class User {
    private int    userId;
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String status;
    private String createdAt;

    public User() {}
    public User(String fullName, String email, String password, String role) {
        this.fullName = fullName; this.email = email;
        this.password = password; this.role  = role; this.status = "ACTIVE";
    }

    public int    getUserId()    { return userId; }
    public void   setUserId(int v)       { userId    = v; }
    public String getFullName()  { return fullName; }
    public void   setFullName(String v)  { fullName  = v; }
    public String getEmail()     { return email; }
    public void   setEmail(String v)     { email     = v; }
    public String getPassword()  { return password; }
    public void   setPassword(String v)  { password  = v; }
    public String getRole()      { return role; }
    public void   setRole(String v)      { role      = v; }
    public String getStatus()    { return status; }
    public void   setStatus(String v)    { status    = v; }
    public String getCreatedAt() { return createdAt; }
    public void   setCreatedAt(String v) { createdAt = v; }
    public boolean isAdmin()     { return "ADMIN".equalsIgnoreCase(role); }
    public boolean isLocked()    { return "LOCKED".equalsIgnoreCase(status); }
}
