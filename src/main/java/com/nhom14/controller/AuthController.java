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
        String[] error = { null };
        User user = authService.login(email, password, error);
        if (user == null) {
            model.addAttribute("error", error[0]);
            return "auth/login";
        }
        session.setAttribute("user", user);
        return user.isAdmin() ? "redirect:/admin/users" : "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

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
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";
        model.addAttribute("user", user);
        return "auth/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";
        String err = authService.changePassword(user.getUserId(), oldPassword, newPassword);
        if (err != null) {
            model.addAttribute("error", err);
            model.addAttribute("user", user);
            return "auth/profile";
        }
        model.addAttribute("success", "Đổi mật khẩu thành công.");
        model.addAttribute("user", user);
        return "auth/profile";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String doForgotPassword(@RequestParam String email, Model model) {
        if (email == null || email.isBlank()) {
            model.addAttribute("error", "Vui lòng nhập email.");
            return "auth/forgot-password";
        }
        String token = authService.generateResetToken(email);
        // Lưu email vào model để hiển thị thông báo
        model.addAttribute("email", email);
        if (token != null) {
            // Trong thực tế sẽ gửi email. Ở đây ta hiển thị luôn cho demo.
            model.addAttribute("demoToken", token);
            model.addAttribute("success", "Mã khôi phục đã được gửi tới email của bạn (Hiệu lực 15 phút).");
        } else {
            // Vẫn hiện success để bảo mật (tránh check email tồn tại)
            model.addAttribute("success", "Nếu email tồn tại trong hệ thống, bạn sẽ nhận được mã khôi phục.");
        }
        return "auth/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String doResetPassword(@RequestParam String token,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
        String err = authService.resetPasswordWithToken(token, newPassword);
        if (err != null) {
            model.addAttribute("error", err);
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
        return "redirect:/login?reset=true";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
            HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";
        String err = authService.updateProfile(user.getUserId(), fullName);
        if (err != null) {
            model.addAttribute("error", err);
        } else {
            user.setFullName(fullName);
            session.setAttribute("user", user);
            model.addAttribute("success", "Cập nhật hồ sơ thành công.");
        }
        model.addAttribute("user", user);
        return "auth/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            authService.deleteAccount(user.getUserId());
            session.invalidate();
        }
        return "redirect:/login?deleted=true";
    }
}
