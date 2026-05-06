package com.nhom14;

import com.nhom14.dao.UserDAO;
import com.nhom14.model.User;
import com.nhom14.service.AuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        // Seed tài khoản admin mặc định nếu chưa tồn tại
        UserDAO userDAO = new UserDAO();
        String adminEmail = "admin@gmail.com";

        if (userDAO.findByEmail(adminEmail) == null) {
            User admin = new User("Hệ thống Admin", adminEmail,
                    AuthService.hash("admin123"), "ADMIN");
            userDAO.insert(admin);
            System.out.println("[SEED] Admin account created successfully!");
        }

        System.out.println();
        System.out.println("Server start successful!");
        System.out.println("Visit at: http://localhost:8080");
        System.out.println("Stop server: Ctrl + C");
        System.out.println();
    }
}
