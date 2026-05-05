package com.nhom14.service;

import com.nhom14.dao.ProgressDAO;
import com.nhom14.dao.TaskDAO;
import com.nhom14.model.Progress;
import com.nhom14.model.Task;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProgressService {

    private final ProgressDAO  progressDAO  = new ProgressDAO();
    private final TaskDAO      taskDAO      = new TaskDAO();

    public Progress findByPlan(int planId) {
        return progressDAO.findByPlan(planId);
    }

    public Map<String, Object> getStats(int userId) {
        List<Task> all = taskDAO.findByUser(userId);
        long total    = all.size();
        long completed = all.stream().filter(t -> "DONE".equals(t.getStatus())).count();
        long inProg    = all.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count();
        long overdue   = all.stream().filter(t -> t.isOverdue() && !"DONE".equals(t.getStatus())).count();
        long upcoming  = all.stream().filter(t -> t.isUpcoming() && !"DONE".equals(t.getStatus())).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("completed", completed);
        stats.put("inProgress", inProg);
        stats.put("overdueCount", overdue);
        stats.put("upcomingCount", upcoming);
        stats.put("rate", total == 0 ? 0 : Math.round((double)completed * 100 / total));
        return stats;
    }

    public List<Task> getUpcomingTasks(int userId) {
        List<Task> result = new ArrayList<>();
        for (Task t : taskDAO.findByUser(userId)) {
            if (t.isUpcoming() && !"DONE".equals(t.getStatus())) result.add(t);
        }
        return result;
    }

    public List<Task> getOverdueTasks(int userId) {
        List<Task> result = new ArrayList<>();
        for (Task t : taskDAO.findByUser(userId)) {
            if (t.isOverdue() && !"DONE".equals(t.getStatus())) result.add(t);
        }
        return result;
    }
}
