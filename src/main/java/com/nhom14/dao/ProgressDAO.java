package com.nhom14.dao;

import com.nhom14.model.DatabaseConnection;
import com.nhom14.model.Progress;
import java.sql.*;

public class ProgressDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Tạo mới record progress khi tạo StudyPlan (1-1) */
    public boolean init(int planId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT OR IGNORE INTO progress(planId,totalTask,completeTask,completeRate) VALUES(?,0,0,0)")) {
            ps.setInt(1, planId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public Progress findByPlan(int planId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM progress WHERE planId=?")) {
            ps.setInt(1, planId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ignored) {}
        return null;
    }

    public boolean update(Progress p) {
        try (PreparedStatement ps = conn().prepareStatement("""
                UPDATE progress SET totalTask=?,completeTask=?,completeRate=?
                WHERE planId=?""")) {
            ps.setInt(1, p.getTotalTask());
            ps.setInt(2, p.getCompleteTask());
            ps.setDouble(3, p.getCompleteRate());
            ps.setInt(4, p.getPlanId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private Progress map(ResultSet rs) throws SQLException {
        Progress p = new Progress();
        p.setProgressId(rs.getInt("progressId"));
        p.setPlanId(rs.getInt("planId"));
        p.setTotalTask(rs.getInt("totalTask"));
        p.setCompleteTask(rs.getInt("completeTask"));
        p.setCompleteRate(rs.getDouble("completeRate"));
        return p;
    }
}
