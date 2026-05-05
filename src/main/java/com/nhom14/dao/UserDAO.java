package com.nhom14.dao;

import com.nhom14.model.DatabaseConnection;
import com.nhom14.model.User;
import java.sql.*;
import java.util.*;

public class UserDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean insert(User u) {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT INTO users(fullName, email, password, roleId, status) VALUES(?,?,?,?,?)")) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setInt(4, "ADMIN".equalsIgnoreCase(u.getRole()) ? 2 : 1);
            ps.setString(5, u.getStatus() != null ? u.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User findByEmail(String email) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT u.*, r.roleName as role FROM users u JOIN roles r ON u.roleId = r.roleId WHERE u.email=?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findById(int id) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT u.*, r.roleName as role FROM users u JOIN roles r ON u.roleId = r.roleId WHERE u.userId=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT u.*, r.roleName as role FROM users u JOIN roles r ON u.roleId = r.roleId ORDER BY u.createdAt DESC")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int userId, String status) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE users SET status=? WHERE userId=?")) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile(int userId, String fullName) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE users SET fullName=? WHERE userId=?")) {
            ps.setString(1, fullName);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int userId, String hashed) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE users SET password=? WHERE userId=?")) {
            ps.setString(1, hashed);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int userId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM users WHERE userId=?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("userId"));
        u.setFullName(rs.getString("fullName"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role")); // From JOIN
        u.setStatus(rs.getString("status"));
        u.setCreatedAt(rs.getString("createdAt"));
        return u;
    }
}
