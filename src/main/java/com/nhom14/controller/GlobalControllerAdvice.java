package com.nhom14.controller;

import com.nhom14.model.User;
import com.nhom14.service.ReminderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * GlobalControllerAdvice: Cung cấp các thuộc tính dùng chung cho tất cả các Controller.
 * Ở đây dùng để lấy số lượng thông báo chưa đọc (unreadReminders) hiển thị trên Nav/Sidebar.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ReminderService reminderService;

    @ModelAttribute
    public void addGlobalAttributes(HttpSession session, Model model) {
        Object userObj = session.getAttribute("user");
        if (userObj instanceof User user) {
            long count = reminderService.findByUser(user.getUserId()).stream()
                    .filter(r -> !r.isRead())
                    .count();
            model.addAttribute("unreadReminders", count);
        }
    }
}
