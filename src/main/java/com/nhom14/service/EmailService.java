package com.nhom14.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    /** Gửi mã OTP khôi phục mật khẩu (Module 1) */
    public void sendOtp(String toEmail, String token) {
        if (mailSender == null) {
            System.out.println("DEBUG: OTP cho " + toEmail + " là: " + token);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("StudyReminder <no-reply@studyreminder.com>");
            message.setTo(toEmail);
            message.setSubject("Mã khôi phục mật khẩu - Study Reminder");
            message.setText("Chào bạn,\n\nMã xác nhận khôi phục mật khẩu của bạn là: " + token + 
                            "\n\nMã này có hiệu lực trong 15 phút. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.");
            mailSender.send(message);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Gửi thông báo nhắc nhở nhiệm vụ sắp đến hạn (Module 4) */
    public void sendTaskReminder(String toEmail, String taskName, String deadline) {
        if (mailSender == null) {
            System.out.println("DEBUG: Nhắc nhở " + taskName + " cho " + toEmail);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("StudyReminder <no-reply@studyreminder.com>");
            message.setTo(toEmail);
            message.setSubject("⏰ NHẮC NHỞ: Nhiệm vụ \"" + taskName + "\" sắp đến hạn!");
            message.setText("Chào bạn,\n\nBạn có nhiệm vụ học tập sắp đến hạn:\n" +
                            "- Nhiệm vụ: " + taskName + "\n" +
                            "- Hạn chót: " + deadline + "\n\n" +
                            "Hãy đăng nhập vào Study Reminder để hoàn thành nhiệm vụ đúng hạn nhé!");
            mailSender.send(message);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
