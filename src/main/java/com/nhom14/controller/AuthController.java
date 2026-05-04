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

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String home(HttpSession session) {
        return session.getAttribute("user") != null ? "redirect:/dashboard" : "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        return session.getAttribute("user") != null ? "redirect:/dashboard" : "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session, Model model) {
        String[] error = {null};
        User user = authService.login(email, password, error);
        if (user == null) { model.addAttribute("error", error[0]); return "auth/login"; }
        session.setAttribute("user", user);
        return user.isAdmin() ? "redirect:/admin/users" : "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerPage() { return "auth/register"; }

    @PostMapping("/register")
    public String doRegister(@RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "auth/register";
        }
        String err = authService.register(fullName, email, password);
        if (err != null) { model.addAttribute("error", err); return "auth/register"; }
        return "redirect:/login?registered=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        return "auth/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        String err = authService.changePassword(user.getUserId(), oldPassword, newPassword);
        if (err != null) { model.addAttribute("error", err); return "auth/profile"; }
        model.addAttribute("success", "Đổi mật khẩu thành công.");
        return "auth/profile";
    }
}
