package com.nhom14.dao;

import com.nhom14.model.DatabaseConnection;
import com.nhom14.model.StudyPlan;
import java.sql.*;
import java.util.*;

public class StudyPlanDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean insert(StudyPlan p) {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT INTO studyPlans(userId,courseId,planName,startDate,endDate) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getUserId());
            if (p.getCourseId() != null) ps.setInt(2, p.getCourseId()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, p.getPlanName());
            ps.setString(4, p.getStartDate());
            ps.setString(5, p.getEndDate());
            if (ps.executeUpdate() > 0) {
                ResultSet gen = ps.getGeneratedKeys();
                if (gen.next()) p.setPlanId(gen.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(StudyPlan p) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE studyPlans SET courseId=?,planName=?,startDate=?,endDate=? WHERE planId=? AND userId=?")) {
            if (p.getCourseId() != null) ps.setInt(1, p.getCourseId()); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, p.getPlanName());
            ps.setString(3, p.getStartDate());
            ps.setString(4, p.getEndDate());
            ps.setInt(5, p.getPlanId());
            ps.setInt(6, p.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean delete(int planId, int userId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM studyPlans WHERE planId=? AND userId=?")) {
            ps.setInt(1, planId); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public StudyPlan findById(int planId, int userId) {
        String sql = """
            SELECT sp.*, c.courseName, p.completeRate, p.totalTask, p.completeTask
            FROM studyPlans sp
            LEFT JOIN courses c ON sp.courseId = c.courseId
            LEFT JOIN progress p ON sp.planId = p.planId
            WHERE sp.planId=? AND sp.userId=?
        """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, planId); ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ignored) {}
        return null;
    }

    public List<StudyPlan> findByUser(int userId) {
        List<StudyPlan> list = new ArrayList<>();
        String sql = """
            SELECT sp.*, c.courseName, 
                   COALESCE(p.completeRate,0) AS completeRate,
                   COALESCE(p.totalTask,0) AS totalTask,
                   COALESCE(p.completeTask,0) AS completeTask
            FROM studyPlans sp
            LEFT JOIN courses c ON sp.courseId = c.courseId
            LEFT JOIN progress p ON sp.planId = p.planId
            WHERE sp.userId=?
            ORDER BY sp.startDate DESC
        """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    public boolean existsName(int userId, String planName, int excludeId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT 1 FROM studyPlans WHERE userId=? AND planName=? AND planId!=?")) {
            ps.setInt(1, userId); ps.setString(2, planName); ps.setInt(3, excludeId);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    private StudyPlan map(ResultSet rs) throws SQLException {
        StudyPlan p = new StudyPlan();
        p.setPlanId(rs.getInt("planId"));
        p.setUserId(rs.getInt("userId"));
        p.setCourseId(rs.getInt("courseId"));
        if (rs.wasNull()) p.setCourseId(null);
        p.setPlanName(rs.getString("planName"));
        p.setStartDate(rs.getString("startDate"));
        p.setEndDate(rs.getString("endDate"));
        p.setCourseName(rs.getString("courseName"));
        try { p.setCompleteRate(rs.getDouble("completeRate")); } catch (SQLException ignored) {}
        try { p.setTotalTask(rs.getInt("totalTask")); }         catch (SQLException ignored) {}
        try { p.setCompleteTask(rs.getInt("completeTask")); }   catch (SQLException ignored) {}
        return p;
    }
}
