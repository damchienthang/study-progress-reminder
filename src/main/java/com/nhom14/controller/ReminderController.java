package com.nhom14.controller;

import com.nhom14.model.Reminder;
import com.nhom14.model.User;
import com.nhom14.service.ReminderService;
import com.nhom14.service.SystemTimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * ReminderController — xử lý danh sách thông báo nhắc nhở (Module 4).
 * Routes:
 *   GET  /reminders            — Danh sách thông báo
 *   POST /reminders/{id}/read  — Đánh dấu đã đọc
 *   POST /reminders/read-all   — Đánh dấu tất cả đã đọc
 *   POST /api/reminder/trigger — Manual trigger (dùng để test TC4-PUSH-1A)
 */
@Controller
public class ReminderController {

    @Autowired private ReminderService     reminderService;
    @Autowired private SystemTimerService  timerService;

    private User currentUser(HttpSession s) {
        return (User) s.getAttribute("user");
    }

    // ── Xem danh sách thông báo ───────────────────────────────────────────
    @GetMapping("/reminders")
    public String list(HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";

        List<Reminder> reminders = reminderService.findByUser(u.getUserId());
        long unread = reminders.stream().filter(r -> !r.isRead()).count();

        model.addAttribute("reminders",    reminders);
        model.addAttribute("unreadCount",  unread);
        return "reminders/list";
    }

    // ── Đánh dấu một thông báo đã đọc ────────────────────────────────────
    @PostMapping("/reminders/{id}/read")
    public String markRead(@PathVariable int id,
                           HttpSession session,
                           RedirectAttributes ra) {
        if (currentUser(session) == null) return "redirect:/login";
        reminderService.markAsRead(id);
        ra.addFlashAttribute("success", "Đã đánh dấu đã đọc.");
        return "redirect:/reminders";
    }

    // ── Đánh dấu tất cả đã đọc ───────────────────────────────────────────
    @PostMapping("/reminders/read-all")
    public String markAllRead(HttpSession session, RedirectAttributes ra) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        reminderService.markAllRead(u.getUserId());
        ra.addFlashAttribute("success", "Đã đánh dấu tất cả thông báo là đã đọc.");
        return "redirect:/reminders";
    }

    // ── Manual trigger cho testing (TC4-PUSH-1A) ─────────────────────────
    @PostMapping("/api/reminder/trigger")
    @ResponseBody
    public String triggerReminder(HttpSession session) {
        if (currentUser(session) == null) return "Unauthorized";
        timerService.checkAndSendReminders();
        return "Reminder check triggered successfully. Check /reminders page.";
    }
}
