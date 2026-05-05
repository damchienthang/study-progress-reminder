package com.nhom14.model;

public class PasswordResetToken {
    private int    tokenId;
    private int    userId;
    private String token;
    private String createdAt;
    private String expiryDate;

    public PasswordResetToken() {}
    public PasswordResetToken(int userId, String token, String expiryDate) {
        this.userId     = userId;
        this.token      = token;
        this.expiryDate = expiryDate;
    }

    public int    getTokenId()              { return tokenId; }
    public void   setTokenId(int v)         { tokenId    = v; }
    public int    getUserId()               { return userId; }
    public void   setUserId(int v)          { userId     = v; }
    public String getToken()                { return token; }
    public void   setToken(String v)        { token      = v; }
    public String getCreatedAt()            { return createdAt; }
    public void   setCreatedAt(String v)    { createdAt  = v; }
    public String getExpiryDate()           { return expiryDate; }
    public void   setExpiryDate(String v)   { expiryDate = v; }
}
