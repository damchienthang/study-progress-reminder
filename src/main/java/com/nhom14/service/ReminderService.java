package com.nhom14.service;

import com.nhom14.dao.ReminderDAO;
import com.nhom14.model.Reminder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReminderService {

    private final ReminderDAO reminderDAO = new ReminderDAO();

    public List<Reminder> findByUser(int userId) {
        return reminderDAO.findByUser(userId);
    }

    public boolean markAsRead(int reminderId) {
        return reminderDAO.markAsRead(reminderId);
    }
}
