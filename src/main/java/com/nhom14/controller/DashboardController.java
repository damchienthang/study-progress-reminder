package com.nhom14.controller;

import com.nhom14.model.User;
import com.nhom14.service.ProgressService;
import com.nhom14.service.ReminderService;
import com.nhom14.service.StudyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired private StudyPlanService planService;
    @Autowired private ProgressService  progressService;
    @Autowired private ReminderService  reminderService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        int uid = u.getUserId();
        
        model.addAttribute("plans",        planService.findByUser(uid));
        model.addAttribute("upcoming",     progressService.getUpcomingTasks(uid));
        model.addAttribute("overdue",      progressService.getOverdueTasks(uid));
        model.addAttribute("reminders",    reminderService.findByUser(uid));
        model.addAttribute("stats",        progressService.getStats(uid));
        
        return "dashboard";
    }

    @PostMapping("/reminders/{reminderId}/read")
    public String markRead(@PathVariable int reminderId, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        reminderService.markAsRead(reminderId);
        return "redirect:/dashboard";
    }
}
