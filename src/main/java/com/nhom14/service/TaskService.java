package com.nhom14.service;

import com.nhom14.dao.ProgressDAO;
import com.nhom14.dao.StudyPlanDAO;
import com.nhom14.dao.TaskDAO;
import com.nhom14.model.Progress;
import com.nhom14.model.StudyPlan;
import com.nhom14.model.Task;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TaskService {

    private static final DateTimeFormatter DB_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TaskDAO      taskDAO  = new TaskDAO();
    private final ProgressDAO  progDAO  = new ProgressDAO();
    private final StudyPlanDAO planDAO  = new StudyPlanDAO();

    public String create(Task t, int userId) {
        if (t.getTaskName() == null || t.getTaskName().isBlank())
            return "Tên nhiệm vụ không được để trống.";
        if (t.getDeadline() == null || t.getDeadline().isBlank())
            return "Deadline không được để trống.";

        // Chuẩn hóa deadline từ HTML (yyyy-MM-ddTHH:mm) sang DB (yyyy-MM-dd HH:mm:ss)
        String rawDl = t.getDeadline().replace("T", " ");
        if (rawDl.length() == 16) rawDl += ":00"; // Thêm giây nếu thiếu
        t.setDeadline(rawDl);

        // Deadline không được là quá khứ
        try {
            LocalDateTime dl = LocalDateTime.parse(t.getDeadline(), DB_FMT);
            if (dl.isBefore(LocalDateTime.now()))
                return "Deadline phải là thời điểm trong tương lai.";
        } catch (Exception e) {
            return "Định dạng deadline không hợp lệ. Vui lòng nhập đúng ngày giờ.";
        }

        // Deadline phải nằm trong khoảng thời gian của StudyPlan
        StudyPlan plan = planDAO.findById(t.getPlanId(), userId);
        if (plan == null) return "Không tìm thấy kế hoạch học tập.";
        
        // So sánh ngày (chỉ lấy phần yyyy-MM-dd)
        String dlDate = t.getDeadline().substring(0, 10);
        if (dlDate.compareTo(plan.getStartDate()) < 0 || dlDate.compareTo(plan.getEndDate()) > 0)
            return "Deadline phải nằm trong thời gian của kế hoạch (" + plan.getStartDate() + " đến " + plan.getEndDate() + ").";

        if (!taskDAO.insert(t)) return "Lỗi hệ thống khi thêm nhiệm vụ.";
        recalcProgress(t.getPlanId());
        return null;
    }

    public String update(Task t) {
        if (t.getTaskName() == null || t.getTaskName().isBlank())
            return "Tên nhiệm vụ không được để trống.";
        
        // Chuẩn hóa deadline
        if (t.getDeadline() != null) {
            String rawDl = t.getDeadline().replace("T", " ");
            if (rawDl.length() == 16) rawDl += ":00";
            t.setDeadline(rawDl);
        }

        return taskDAO.update(t) ? null : "Lỗi hệ thống khi cập nhật nhiệm vụ.";
    }

    /** Cập nhật trạng thái: ghi nhận completedAt nếu chuyển sang DONE */
    public String updateStatus(int taskId, String status) {
        Task t = taskDAO.findById(taskId);
        if (t == null) return "Không tìm thấy nhiệm vụ.";
        String completedAt = null;
        if ("DONE".equals(status)) {
            completedAt = LocalDateTime.now().format(DB_FMT);
        }
        if (!taskDAO.updateStatus(taskId, status, completedAt))
            return "Lỗi hệ thống khi cập nhật trạng thái.";
        recalcProgress(t.getPlanId());
        return null;
    }

    public boolean delete(int taskId) {
        Task t = taskDAO.findById(taskId);
        if (t == null) return false;
        boolean ok = taskDAO.delete(taskId);
        if (ok) recalcProgress(t.getPlanId());
        return ok;
    }

    public Task findById(int taskId) {
        return taskDAO.findById(taskId);
    }

    public List<Task> findByPlan(int planId) {
        return taskDAO.findByPlan(planId);
    }

    public List<Task> findByUser(int userId) {
        return taskDAO.findByUser(userId);
    }

    /** Tính lại progress sau mỗi thay đổi task */
    private void recalcProgress(int planId) {
        int total    = taskDAO.countByPlan(planId);
        int complete = taskDAO.countDoneByPlan(planId);
        Progress p   = progDAO.findByPlan(planId);
        if (p == null) { progDAO.init(planId); p = progDAO.findByPlan(planId); }
        if (p == null) return;
        p.setTotalTask(total);
        p.setCompleteTask(complete);
        p.recalculate();
        progDAO.update(p);
    }
}
