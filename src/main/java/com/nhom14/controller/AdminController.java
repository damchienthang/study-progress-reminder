package com.nhom14.controller;

import com.nhom14.dao.UserDAO;
import com.nhom14.model.User;
import com.nhom14.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserDAO userDAO = new UserDAO();

    // ── Guard: chỉ admin mới vào được ─────────────────────────────────────
    private User requireAdmin(HttpSession session) {
        Object obj = session.getAttribute("user");
        if (obj instanceof User u && u.isAdmin()) return u;
        return null;
    }

    // ── /admin → redirect sang /admin/users ──────────────────────────────
    @GetMapping({"", "/"})
    public String adminRoot(HttpSession session) {
        if (requireAdmin(session) == null) return "redirect:/login";
        return "redirect:/admin/users";
    }

    // ── Trang quản lý người dùng ─────────────────────────────────────────
    @GetMapping("/users")
    public String usersPage(HttpSession session, Model model,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String role,
                            @RequestParam(required = false) String status) {
        User admin = requireAdmin(session);
        if (admin == null) return "redirect:/login";

        List<User> allUsers = userDAO.findAll();
        List<User> users = allUsers;

        // Lọc theo search / role / status
        if (search != null && !search.isBlank()) {
            String kw = search.toLowerCase();
            users = users.stream()
                    .filter(u -> u.getFullName().toLowerCase().contains(kw)
                              || u.getEmail().toLowerCase().contains(kw))
                    .toList();
        }
        if (role != null && !role.isBlank()) {
            users = users.stream()
                    .filter(u -> role.equalsIgnoreCase(u.getRole()))
                    .toList();
        }
        if (status != null && !status.isBlank()) {
            users = users.stream()
                    .filter(u -> status.equalsIgnoreCase(u.getStatus()))
                    .toList();
        }

        long totalUsers    = allUsers.size();
        long totalStudents = allUsers.stream().filter(u -> "STUDENT".equalsIgnoreCase(u.getRole())).count();
        long totalAdmins   = allUsers.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getRole())).count();
        long lockedCount   = allUsers.stream().filter(User::isLocked).count();

        model.addAttribute("users",         users);
        model.addAttribute("totalUsers",    totalUsers);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalAdmins",   totalAdmins);
        model.addAttribute("lockedCount",   lockedCount);
        model.addAttribute("search",        search);
        model.addAttribute("roleFilter",    role);
        model.addAttribute("statusFilter",  status);
        model.addAttribute("currentUserId", admin.getUserId()); // dùng trong template
        return "admin/users";
    }

    // ── Khóa / Mở khóa tài khoản ─────────────────────────────────────────
    @PostMapping("/users/{id}/toggle-lock")
    public String toggleLock(@PathVariable int id,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (requireAdmin(session) == null) return "redirect:/login";

        User admin = (User) session.getAttribute("user");
        if (admin.getUserId() == id) {
            ra.addFlashAttribute("error", "Không thể khóa chính tài khoản admin đang đăng nhập.");
            return "redirect:/admin/users";
        }

        User u = userDAO.findById(id);
        if (u == null) { ra.addFlashAttribute("error", "Không tìm thấy người dùng."); return "redirect:/admin/users"; }

        String newStatus = u.isLocked() ? "ACTIVE" : "LOCKED";
        userDAO.updateStatus(id, newStatus);
        ra.addFlashAttribute("success",
                u.isLocked() ? "Đã mở khóa tài khoản " + u.getFullName()
                             : "Đã khóa tài khoản " + u.getFullName());
        return "redirect:/admin/users";
    }

    // ── Xóa tài khoản ────────────────────────────────────────────────────
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable int id,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (requireAdmin(session) == null) return "redirect:/login";

        User admin = (User) session.getAttribute("user");
        if (admin.getUserId() == id) {
            ra.addFlashAttribute("error", "Không thể xóa chính tài khoản admin đang đăng nhập.");
            return "redirect:/admin/users";
        }

        User u = userDAO.findById(id);
        if (u == null) { ra.addFlashAttribute("error", "Không tìm thấy người dùng."); return "redirect:/admin/users"; }

        userDAO.delete(id);
        ra.addFlashAttribute("success", "Đã xóa tài khoản " + u.getFullName());
        return "redirect:/admin/users";
    }

    // ── Đặt lại mật khẩu ─────────────────────────────────────────────────
    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(@PathVariable int id,
                                @RequestParam String newPassword,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (requireAdmin(session) == null) return "redirect:/login";
        if (newPassword == null || newPassword.length() < 6) {
            ra.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            return "redirect:/admin/users";
        }
        User u = userDAO.findById(id);
        if (u == null) { ra.addFlashAttribute("error", "Không tìm thấy người dùng."); return "redirect:/admin/users"; }

        userDAO.updatePassword(id, AuthService.hash(newPassword));
        ra.addFlashAttribute("success", "Đã đặt lại mật khẩu cho " + u.getFullName());
        return "redirect:/admin/users";
    }
}
