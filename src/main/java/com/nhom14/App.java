package com.nhom14;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println();
        System.out.println("=== Study Progress Reminder ===");
        System.out.println("Server khởi động thành công!");
        System.out.println("Truy cập tại: http://localhost:8080");
        System.out.println("Dừng server: Ctrl + C");
        System.out.println("===============================");
        System.out.println();
    }
}
