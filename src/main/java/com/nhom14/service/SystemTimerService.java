package com.nhom14.service;

import com.nhom14.dao.ReminderDAO;
import com.nhom14.dao.TaskDAO;
import com.nhom14.model.Reminder;
import com.nhom14.model.Task;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * SystemTimerService: chạy ngầm định kỳ (mỗi 5 phút), quét Task sắp đến hạn < 24h
 * và tạo Reminder tự động — giới hạn không quá 2 thông báo / task / 24h.
 */
@Service
public class SystemTimerService {

    private final TaskDAO     taskDAO     = new TaskDAO();
    private final ReminderDAO reminderDAO = new ReminderDAO();

    /** Chạy mỗi 5 phút */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void checkAndSendReminders() {
        List<Task> upcoming = taskDAO.findUpcomingForAll();
        for (Task t : upcoming) {
            // Lấy userId qua join studyPlans (đã có planId trên task)
            int userId = getUserIdByPlanId(t.getPlanId());
            if (userId <= 0) continue;
            // Giới hạn 2 thông báo / task / 24h
            if (reminderDAO.countSentLast24h(t.getTaskId()) >= 2) continue;
            String msg = "⚠️ Nhắc nhở: Nhiệm vụ \"" + t.getTaskName()
                    + "\" sẽ đến hạn lúc " + t.getDeadline() + ". Hãy hoàn thành sớm!";
            reminderDAO.insert(new Reminder(t.getTaskId(), userId, msg));
        }
    }

    /** Lấy userId từ planId thông qua JDBC trực tiếp */
    private int getUserIdByPlanId(int planId) {
        try (var ps = com.nhom14.model.DatabaseConnection.getInstance().getConnection()
                .prepareStatement("SELECT userId FROM studyPlans WHERE planId=?")) {
            ps.setInt(1, planId);
            var rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) { return 0; }
    }
}
