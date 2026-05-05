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
                "INSERT INTO users(fullName,studentId,username,email,password,roleId,status) VALUES(?,?,?,?,?,?,?)")) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getStudentId());
            ps.setString(3, u.getUsername());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPassword());
            ps.setInt(6, u.getRoleId());
            ps.setString(7, u.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public User findByEmail(String email) {
        String sql = "SELECT u.*, r.roleName FROM users u LEFT JOIN roles r ON u.roleId = r.roleId WHERE LOWER(u.email) = LOWER(?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ignored) {}
        return null;
    }

    public User findById(int id) {
        String sql = "SELECT u.*, r.roleName FROM users u JOIN roles r ON u.roleId = r.roleId WHERE u.userId = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ignored) {}
        return null;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.roleName FROM users u JOIN roles r ON u.roleId = r.roleId ORDER BY u.createdAt DESC";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    public boolean updateProfile(User u) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE users SET fullName=?, studentId=?, username=? WHERE userId=?")) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getStudentId());
            ps.setString(3, u.getUsername());
            ps.setInt(4, u.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateStatus(int userId, String status) {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE users SET status=? WHERE userId=?")) {
            ps.setString(1, status); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updatePassword(int userId, String hashed) {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE users SET password=? WHERE userId=?")) {
            ps.setString(1, hashed); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean delete(int userId) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM users WHERE userId=?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("userId"));
        u.setFullName(rs.getString("fullName"));
        u.setStudentId(rs.getString("studentId"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRoleId(rs.getInt("roleId"));
        u.setRoleName(rs.getString("roleName"));
        u.setStatus(rs.getString("status"));
        u.setCreatedAt(rs.getString("createdAt"));
        return u;
    }
}
