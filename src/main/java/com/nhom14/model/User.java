package com.nhom14.model;

public class User {
    private int    userId;
    private String fullName;
    private String studentId;
    private String username;
    private String email;
    private String password;
    private int    roleId;
    private String roleName;   // join từ bảng roles để hiển thị
    private String status;
    private String createdAt;

    public User() {}
    public User(String fullName, String email, String password, int roleId) {
        this.fullName  = fullName;
        this.email     = email;
        this.password  = password;
        this.roleId    = roleId;
        this.status    = "ACTIVE";
    }

    public int    getUserId()              { return userId; }
    public void   setUserId(int v)         { userId    = v; }
    public String getFullName()            { return fullName; }
    public void   setFullName(String v)    { fullName  = v; }
    public String getStudentId()           { return studentId; }
    public void   setStudentId(String v)   { studentId = v; }
    public String getUsername()            { return username; }
    public void   setUsername(String v)    { username  = v; }
    public String getEmail()               { return email; }
    public void   setEmail(String v)       { email     = v; }
    public String getPassword()            { return password; }
    public void   setPassword(String v)    { password  = v; }
    public int    getRoleId()              { return roleId; }
    public void   setRoleId(int v)         { roleId    = v; }
    public String getRoleName()            { return roleName; }
    public void   setRoleName(String v)    { roleName  = v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { status    = v; }
    public String getCreatedAt()           { return createdAt; }
    public void   setCreatedAt(String v)   { createdAt = v; }

    public boolean isAdmin()  { return roleId == 2 || "ADMIN".equalsIgnoreCase(roleName); }
    public boolean isLocked() { return "LOCKED".equalsIgnoreCase(status); }
    public String  getRole()  { return roleName != null ? roleName : (roleId == 2 ? "ADMIN" : "STUDENT"); }
}
