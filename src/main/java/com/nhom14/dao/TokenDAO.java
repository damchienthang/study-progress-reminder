package com.nhom14.dao;

import com.nhom14.model.DatabaseConnection;
import com.nhom14.model.PasswordResetToken;
import java.sql.*;

public class TokenDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean insert(PasswordResetToken t) {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT INTO passwordResetTokens(userId, token, expiryDate) VALUES(?,?,?)")) {
            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getToken());
            ps.setString(3, t.getExpiryDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PasswordResetToken findByToken(String token) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM passwordResetTokens WHERE token=?")) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PasswordResetToken t = new PasswordResetToken();
                t.setTokenId(rs.getInt("tokenId"));
                t.setUserId(rs.getInt("userId"));
                t.setToken(rs.getString("token"));
                t.setCreatedAt(rs.getString("createdAt"));
                t.setExpiryDate(rs.getString("expiryDate"));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteByUser(int userId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM passwordResetTokens WHERE userId=?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
