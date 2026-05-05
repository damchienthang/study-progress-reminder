package com.nhom14.dao;

import com.nhom14.model.DatabaseConnection;
import com.nhom14.model.Reminder;
import java.sql.*;
import java.util.*;

public class ReminderDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean insert(Reminder r) {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT INTO reminders(taskId,userId,message) VALUES(?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getTaskId());
            ps.setInt(2, r.getUserId());
            ps.setString(3, r.getMessage());
            if (ps.executeUpdate() > 0) {
                ResultSet gen = ps.getGeneratedKeys();
                if (gen.next()) r.setReminderId(gen.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Reminder> findByUser(int userId) {
        List<Reminder> list = new ArrayList<>();
        String sql = """
            SELECT r.*, t.taskName FROM reminders r
            JOIN tasks t ON r.taskId = t.taskId
            WHERE r.userId=?
            ORDER BY r.sentAt DESC
        """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    public boolean markAsRead(int reminderId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE reminders SET isRead=1 WHERE reminderId=?")) {
            ps.setInt(1, reminderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    /** Đếm số thông báo đã gửi cho task trong vòng 24h — giới hạn không quá 2 */
    public int countSentLast24h(int taskId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT COUNT(*) FROM reminders WHERE taskId=? AND sentAt > datetime('now','-24 hours')")) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    private Reminder map(ResultSet rs) throws SQLException {
        Reminder r = new Reminder();
        r.setReminderId(rs.getInt("reminderId"));
        r.setTaskId(rs.getInt("taskId"));
        r.setUserId(rs.getInt("userId"));
        r.setMessage(rs.getString("message"));
        r.setSentAt(rs.getString("sentAt"));
        r.setRead(rs.getInt("isRead") == 1);
        try { r.setTaskName(rs.getString("taskName")); } catch (SQLException ignored) {}
        return r;
    }
}
