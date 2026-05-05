package com.nhom14.service;

import com.nhom14.dao.UserDAO;
import com.nhom14.model.User;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public String register(String fullName, String email, String password) {
        if (fullName == null || fullName.isBlank()) return "Vui lòng nhập họ tên.";
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-z]{2,}$"))
            return "Email không hợp lệ.";
        if (password == null || password.length() < 6)
            return "Mật khẩu phải có ít nhất 6 ký tự.";
        if (userDAO.findByEmail(email.toLowerCase()) != null)
            return "Email này đã được đăng ký.";
        return userDAO.insert(new User(fullName.trim(), email.toLowerCase(),
                hash(password), "STUDENT")) ? null : "Lỗi hệ thống.";
    }

    public User login(String email, String password, String[] error) {
        if (email == null || email.isBlank())     { error[0] = "Vui lòng nhập email.";    return null; }
        if (password == null || password.isBlank()){ error[0] = "Vui lòng nhập mật khẩu."; return null; }
        User u = userDAO.findByEmail(email.toLowerCase());
        if (u == null || !hash(password).equals(u.getPassword())) {
            error[0] = "Tài khoản hoặc mật khẩu không chính xác."; return null;
        }
        if (u.isLocked()) { error[0] = "Tài khoản đã bị khóa. Liên hệ Admin."; return null; }
        return u;
    }

    public String changePassword(int userId, String oldPwd, String newPwd) {
        User u = userDAO.findById(userId);
        if (u == null) return "Không tìm thấy tài khoản.";
        if (!hash(oldPwd).equals(u.getPassword())) return "Mật khẩu cũ không đúng.";
        if (newPwd == null || newPwd.length() < 6) return "Mật khẩu mới phải có ít nhất 6 ký tự.";
        return userDAO.updatePassword(userId, hash(newPwd)) ? null : "Lỗi hệ thống.";
    }

    public String updateProfile(int userId, String fullName) {
        if (fullName == null || fullName.isBlank()) return "Vui lòng nhập họ tên.";
        return userDAO.updateProfile(userId, fullName.trim()) ? null : "Lỗi hệ thống.";
    }

    public String forgotPassword(String email, String newPwd) {
        if (email == null || email.isBlank()) return "Vui lòng nhập email.";
        User u = userDAO.findByEmail(email.toLowerCase());
        if (u == null) return "Email không tồn tại trong hệ thống.";
        if (newPwd == null || newPwd.length() < 6) return "Mật khẩu mới phải có ít nhất 6 ký tự.";
        return userDAO.updatePassword(u.getUserId(), hash(newPwd)) ? null : "Lỗi hệ thống.";
    }

    public boolean deleteAccount(int userId) {
        return userDAO.delete(userId);
    }

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
