package com.nhom14.service;

import com.nhom14.dao.UserDAO;
import com.nhom14.model.User;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        if (email == null || password == null) return null;
        User u = userDAO.findByEmail(email.trim());
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    public String register(User u) {
        if (userDAO.findByEmail(u.getEmail()) != null) {
            return "Email này đã được sử dụng.";
        }
        return userDAO.insert(u) ? null : "Lỗi hệ thống khi đăng ký.";
    }

    public String updateProfile(User u) {
        return userDAO.updateProfile(u) ? null : "Lỗi hệ thống khi cập nhật hồ sơ.";
    }

    public String changePassword(int userId, String oldPass, String newPass) {
        User u = userDAO.findById(userId);
        if (u == null || !u.getPassword().equals(oldPass)) {
            return "Mật khẩu cũ không chính xác.";
        }
        if (newPass.length() < 6) return "Mật khẩu mới phải từ 6 ký tự.";
        return userDAO.updatePassword(userId, newPass) ? null : "Lỗi hệ thống khi đổi mật khẩu.";
    }

    public void deleteAccount(int userId) {
        userDAO.delete(userId);
    }

    public User findById(int userId) {
        return userDAO.findById(userId);
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }

    public void updateStatus(int userId, String status) {
        userDAO.updateStatus(userId, status);
    }
}
