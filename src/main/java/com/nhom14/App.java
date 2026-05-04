package com.nhom14;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println();
        System.out.println("Server khởi động thành công!");
        System.out.println("Truy cập tại: ");
        System.out.println("http://localhost:8080");
        System.out.println("Dừng server: nhấn Ctrl + C");
        System.out.println();
    }
}
