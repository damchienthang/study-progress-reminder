package com.nhom14.controller;

import com.nhom14.model.User;
import com.nhom14.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired private AuthService authService;
    
    @GetMapping("/")
    public String index() { return "redirect:/login"; }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) return "redirect:/dashboard";
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, 
                        HttpSession session, Model model) {
        User u = authService.login(email, password);
        if (u == null) {
            model.addAttribute("error", "Email hoặc mật khẩu không chính xác.");
            return "auth/login";
        }
        if (u.isLocked()) {
            model.addAttribute("error", "Tài khoản của bạn đã bị khóa.");
            return "auth/login";
        }
        session.setAttribute("user", u);
        return u.isAdmin() ? "redirect:/admin" : "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("user") != null) return "redirect:/dashboard";
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName, @RequestParam String email, 
                           @RequestParam String password, @RequestParam String confirmPassword, 
                           Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "auth/register";
        }
        User u = new User(fullName, email, password, 1);
        String err = authService.register(u);
        if (err != null) {
            model.addAttribute("error", err);
            return "auth/register";
        }
        return "redirect:/login?registered=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        // Lấy lại từ DB để có data mới nhất
        model.addAttribute("user", authService.findById(u.getUserId()));
        return "auth/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName, 
                                @RequestParam(required = false) String studentId,
                                @RequestParam(required = false) String username,
                                HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        
        u.setFullName(fullName);
        u.setStudentId(studentId);
        u.setUsername(username);
        
        String err = authService.updateProfile(u);
        if (err != null) {
            model.addAttribute("error", err);
            return "auth/profile";
        }
        session.setAttribute("user", u);
        model.addAttribute("user", authService.findById(u.getUserId()));
        model.addAttribute("success", "Cập nhật hồ sơ thành công!");
        return "auth/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, 
                                 HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        String err = authService.changePassword(u.getUserId(), oldPassword, newPassword);
        if (err != null) {
            model.addAttribute("error", err);
        } else {
            model.addAttribute("success", "Đổi mật khẩu thành công!");
        }
        model.addAttribute("user", authService.findById(u.getUserId()));
        return "auth/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u != null) authService.deleteAccount(u.getUserId());
        session.invalidate();
        return "redirect:/login?deleted=true";
    }
}
