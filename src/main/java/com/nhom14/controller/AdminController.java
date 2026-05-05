package com.nhom14.controller;

import com.nhom14.dao.UserDAO;
import com.nhom14.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserDAO userDAO = new UserDAO();

    private User requireAdmin(HttpSession session) {
        Object o = session.getAttribute("user");
        if (!(o instanceof User)) return null;
        User u = (User) o;
        return u.isAdmin() ? u : null;
    }

    @GetMapping({"", "/", "/users"})
    public String userList(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/login";
        model.addAttribute("users", userDAO.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{userId}/lock")
    public String lockUser(@PathVariable int userId, HttpSession session) {
        if (requireAdmin(session) == null) return "redirect:/login";
        userDAO.updateStatus(userId, "LOCKED");
        return "redirect:/admin/users?msg=locked";
    }

    @PostMapping("/users/{userId}/unlock")
    public String unlockUser(@PathVariable int userId, HttpSession session) {
        if (requireAdmin(session) == null) return "redirect:/login";
        userDAO.updateStatus(userId, "ACTIVE");
        return "redirect:/admin/users?msg=unlocked";
    }

    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable int userId, HttpSession session) {
        if (requireAdmin(session) == null) return "redirect:/login";
        userDAO.delete(userId);
        return "redirect:/admin/users?msg=deleted";
    }
}
