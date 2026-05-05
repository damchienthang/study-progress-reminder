package com.nhom14.dao;

import com.nhom14.model.DatabaseConnection;
import com.nhom14.model.Task;
import java.sql.*;
import java.util.*;

public class TaskDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean insert(Task t) {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT INTO tasks(planId,courseId,taskName,description,deadline,priority,status) VALUES(?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getPlanId());
            ps.setObject(2, t.getCourseId() > 0 ? t.getCourseId() : null);
            ps.setString(3, t.getTaskName());
            ps.setString(4, t.getDescription());
            ps.setString(5, t.getDeadline());
            ps.setString(6, t.getPriority());
            ps.setString(7, "TODO");
            if (ps.executeUpdate() > 0) {
                ResultSet gen = ps.getGeneratedKeys();
                if (gen.next()) t.setTaskId(gen.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Task t) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE tasks SET taskName=?,description=?,deadline=?,priority=?,courseId=? WHERE taskId=?")) {
            ps.setString(1, t.getTaskName());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getDeadline());
            ps.setString(4, t.getPriority());
            ps.setObject(5, t.getCourseId() > 0 ? t.getCourseId() : null);
            ps.setInt(6, t.getTaskId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateStatus(int taskId, String status, String completedAt) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE tasks SET status=?,completedAt=? WHERE taskId=?")) {
            ps.setString(1, status);
            ps.setString(2, completedAt);
            ps.setInt(3, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean delete(int taskId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM tasks WHERE taskId=?")) {
            ps.setInt(1, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public void deleteByCourse(int courseId) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM tasks WHERE courseId=?")) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Task findById(int taskId) {
        String sql = """
            SELECT t.*, c.courseName FROM tasks t
            LEFT JOIN courses c ON t.courseId = c.courseId
            WHERE t.taskId=?
        """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ignored) {}
        return null;
    }

    public List<Task> findByPlan(int planId) {
        List<Task> list = new ArrayList<>();
        String sql = """
            SELECT t.*, c.courseName FROM tasks t
            LEFT JOIN courses c ON t.courseId = c.courseId
            WHERE t.planId=?
            ORDER BY t.deadline ASC
        """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, planId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    // Lấy tất cả task của user (join qua studyPlans)
    public List<Task> findByUser(int userId) {
        List<Task> list = new ArrayList<>();
        String sql = """
            SELECT t.*, c.courseName FROM tasks t
            JOIN studyPlans sp ON t.planId = sp.planId
            LEFT JOIN courses c ON t.courseId = c.courseId
            WHERE sp.userId=?
            ORDER BY t.deadline ASC
        """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    // Lấy task sắp đến hạn (< 24h) của toàn bộ user, chưa DONE
    public List<Task> findUpcomingForAll() {
        List<Task> list = new ArrayList<>();
        String sql = """
            SELECT t.*, c.courseName FROM tasks t
            LEFT JOIN courses c ON t.courseId = c.courseId
            WHERE t.status != 'DONE'
              AND t.deadline > datetime('now')
              AND t.deadline <= datetime('now', '+24 hours')
        """;
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    // Thống kê theo planId
    public int countByPlan(int planId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT COUNT(*) FROM tasks WHERE planId=?")) {
            ps.setInt(1, planId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    public int countDoneByPlan(int planId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT COUNT(*) FROM tasks WHERE planId=? AND status='DONE'")) {
            ps.setInt(1, planId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    private Task map(ResultSet rs) throws SQLException {
        Task t = new Task();
        t.setTaskId(rs.getInt("taskId"));
        t.setPlanId(rs.getInt("planId"));
        t.setTaskName(rs.getString("taskName"));
        t.setDescription(rs.getString("description"));
        t.setDeadline(rs.getString("deadline"));
        t.setPriority(rs.getString("priority"));
        t.setStatus(rs.getString("status"));
        t.setCompletedAt(rs.getString("completedAt"));
        try { t.setCourseId(rs.getInt("courseId")); }   catch (SQLException ignored) {}
        try { t.setCourseName(rs.getString("courseName")); } catch (SQLException ignored) {}
        return t;
    }
}
