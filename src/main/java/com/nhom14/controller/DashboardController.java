package com.nhom14.controller;

import com.nhom14.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        Object obj = session.getAttribute("user");
        if (obj == null) return "redirect:/login";
        // Admin không dùng dashboard của student
        if (obj instanceof User u && u.isAdmin()) return "redirect:/admin/users";
        return "dashboard";
    }
}
