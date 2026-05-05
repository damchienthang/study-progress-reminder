package com.nhom14.dao;

import com.nhom14.model.Course;
import com.nhom14.model.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class CourseDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean insert(Course c) {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT INTO courses(userId,courseName,courseCode,lecturer,credits,semester) VALUES(?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getUserId());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getCourseCode());
            ps.setString(4, c.getLecturer());
            ps.setInt(5, c.getCredits());
            ps.setString(6, c.getSemester());
            if (ps.executeUpdate() > 0) {
                ResultSet gen = ps.getGeneratedKeys();
                if (gen.next()) c.setCourseId(gen.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Course c) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE courses SET courseName=?,courseCode=?,lecturer=?,credits=?,semester=? WHERE courseId=? AND userId=?")) {
            ps.setString(1, c.getCourseName());
            ps.setString(2, c.getCourseCode());
            ps.setString(3, c.getLecturer());
            ps.setInt(4, c.getCredits());
            ps.setString(5, c.getSemester());
            ps.setInt(6, c.getCourseId());
            ps.setInt(7, c.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean delete(int courseId, int userId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM courses WHERE courseId=? AND userId=?")) {
            ps.setInt(1, courseId); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public Course findById(int courseId, int userId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM courses WHERE courseId=? AND userId=?")) {
            ps.setInt(1, courseId); ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ignored) {}
        return null;
    }

    public List<Course> findByUser(int userId) {
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM courses WHERE userId=? ORDER BY courseName ASC")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    public List<Course> findByUserAndSemester(int userId, String semester) {
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM courses WHERE userId=? AND semester=? ORDER BY courseName ASC")) {
            ps.setInt(1, userId); ps.setString(2, semester);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    /** Tìm môn học theo tên (search) */
    public List<Course> findByUserAndSearch(int userId, String keyword) {
        List<Course> list = new ArrayList<>();
        String kw = "%" + keyword.toLowerCase() + "%";
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM courses WHERE userId=? AND (LOWER(courseName) LIKE ? OR LOWER(courseCode) LIKE ?) ORDER BY courseName ASC")) {
            ps.setInt(1, userId); ps.setString(2, kw); ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    /** Tìm môn học kết hợp search + semester */
    public List<Course> findByUserSemesterAndSearch(int userId, String semester, String keyword) {
        List<Course> list = new ArrayList<>();
        String kw = "%" + keyword.toLowerCase() + "%";
        String semFilter = (semester != null && !semester.isBlank()) ? semester : null;
        String sql = "SELECT * FROM courses WHERE userId=? AND (LOWER(courseName) LIKE ? OR LOWER(courseCode) LIKE ?)"
                + (semFilter != null ? " AND semester=?" : "") + " ORDER BY courseName ASC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setString(2, kw); ps.setString(3, kw);
            if (semFilter != null) ps.setString(4, semFilter);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    public boolean existsCode(int userId, String courseCode, int excludeId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT 1 FROM courses WHERE userId=? AND courseCode=? AND courseId!=?")) {
            ps.setInt(1, userId); ps.setString(2, courseCode); ps.setInt(3, excludeId);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    private Course map(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("courseId"));
        c.setUserId(rs.getInt("userId"));
        c.setCourseName(rs.getString("courseName"));
        c.setCourseCode(rs.getString("courseCode"));
        c.setLecturer(rs.getString("lecturer"));
        c.setCredits(rs.getInt("credits"));
        c.setSemester(rs.getString("semester"));
        return c;
    }
}
