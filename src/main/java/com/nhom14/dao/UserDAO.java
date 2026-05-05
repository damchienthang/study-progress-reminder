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
                "INSERT INTO users(full_name,email,password,role,status) VALUES(?,?,?,?,?)")) {
            ps.setString(1, u.getFullName()); ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword()); ps.setString(4, u.getRole());
            ps.setString(5, u.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public User findByEmail(String email) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM users WHERE email=?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ignored) {}
        return null;
    }

    public User findById(int id) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM users WHERE user_id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ignored) {}
        return null;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users ORDER BY created_at DESC")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    public boolean updateStatus(int userId, String status) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE users SET status=? WHERE user_id=?")) {
            ps.setString(1, status); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateProfile(int userId, String fullName) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE users SET full_name=? WHERE user_id=?")) {
            ps.setString(1, fullName); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updatePassword(int userId, String hashed) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE users SET password=? WHERE user_id=?")) {
            ps.setString(1, hashed); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean delete(int userId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM users WHERE user_id=?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        u.setCreatedAt(rs.getString("created_at"));
        return u;
    }
}
