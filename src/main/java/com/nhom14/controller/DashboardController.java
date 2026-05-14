package com.nhom14.controller;

import com.nhom14.dao.CourseDAO;
import com.nhom14.dao.ProgressDAO;
import com.nhom14.dao.ReminderDAO;
import com.nhom14.dao.StudyPlanDAO;
import com.nhom14.dao.TaskDAO;
import com.nhom14.model.*;
import com.nhom14.service.ProgressService;
import com.nhom14.service.ReminderService;
import com.nhom14.service.StudyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import java.util.*;

@Controller
public class DashboardController {

    @Autowired private ProgressService progressService;
    @Autowired private StudyPlanService planService;
    @Autowired private ReminderService reminderService;

    private final CourseDAO   courseDAO   = new CourseDAO();
    private final TaskDAO     taskDAO     = new TaskDAO();
    private final StudyPlanDAO planDAO    = new StudyPlanDAO();
    private final ProgressDAO  progressDAO = new ProgressDAO();
    private final ReminderDAO  reminderDAO = new ReminderDAO();

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object obj = session.getAttribute("user");
        if (obj == null) return "redirect:/login";
        if (obj instanceof User u && u.isAdmin()) return "redirect:/admin/users";

        User user = (User) obj;
        int uid = user.getUserId();

        // ── Stats cards ──────────────────────────────────────────────
        List<com.nhom14.model.Course>    courses = courseDAO.findByUser(uid);
        List<Task>   tasks   = taskDAO.findByUser(uid);
        long totalCourses = courses.size();
        long totalTasks   = tasks.size();
        long doneTasks    = tasks.stream().filter(t -> "DONE".equals(t.getStatus())).count();
        long overdueTasks = tasks.stream().filter(t -> t.isOverdue()).count();
        long progressPct  = totalTasks == 0 ? 0 : Math.round((double) doneTasks * 100 / totalTasks);

        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalTasks",   totalTasks);
        model.addAttribute("doneTasks",    doneTasks);
        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("progressPct",  progressPct);

        // unreadReminders is already handled by GlobalControllerAdvice
        // ── Course progress list ──────────────────────────────────────
        List<Map<String, Object>> courseProgressList = new ArrayList<>();
        for (com.nhom14.model.Course c : courses) {
            List<Task> ct = tasks.stream()
                    .filter(t -> t.getCourseId() == c.getCourseId())
                    .toList();
            long total = ct.size();
            long done  = ct.stream().filter(t -> "DONE".equals(t.getStatus())).count();
            long pct   = total == 0 ? 0 : Math.round((double) done * 100 / total);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("courseName", c.getCourseName());
            row.put("pct",  pct);
            row.put("done", done);
            row.put("total", total);
            courseProgressList.add(row);
        }
        model.addAttribute("courseProgressList", courseProgressList);

        // ── Attention tasks (overdue + upcoming), max 10 ─────────────
        List<Map<String, Object>> attentionTasks = new ArrayList<>();
        for (Task t : tasks) {
            boolean over = t.isOverdue();
            boolean up   = t.isUpcoming();
            if (!over && !up) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name",          t.getTaskName());
            row.put("courseName",    t.getCourseName() != null ? t.getCourseName() : "—");
            row.put("overdue",       over);
            String dl = t.getDeadline();
            row.put("deadlineDisplay", (dl != null && dl.length() >= 16)
                    ? dl.substring(0, 16).replace("T", " ") : (dl != null ? dl : ""));
            row.put("planId",        t.getPlanId());
            attentionTasks.add(row);
            if (attentionTasks.size() >= 10) break;
        }
        model.addAttribute("attentionTasks", attentionTasks);

        // ── Study plans with progress ─────────────────────────────────
        List<StudyPlan> plans = planDAO.findByUser(uid);
        List<Map<String, Object>> dashPlans = new ArrayList<>();
        for (StudyPlan p : plans) {
            Progress prog = progressDAO.findByPlan(p.getPlanId());
            long total = prog != null ? prog.getTotalTask()    : 0;
            long done  = prog != null ? prog.getCompleteTask() : 0;
            long pct   = total == 0 ? 0 : Math.round((double) done * 100 / total);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("planId",       p.getPlanId());
            row.put("name",         p.getPlanName());
            row.put("startDateDisplay", p.getStartDate());
            row.put("endDateDisplay",   p.getEndDate());
            row.put("pct",  pct);
            row.put("done", done);
            row.put("total", total);
            dashPlans.add(row);
        }
        model.addAttribute("dashPlans", dashPlans);

        return "dashboard";
    }
}
