package com.nhom14.service;

import com.nhom14.dao.ProgressDAO;
import com.nhom14.dao.StudyPlanDAO;
import com.nhom14.model.StudyPlan;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudyPlanService {

    private final StudyPlanDAO planDAO  = new StudyPlanDAO();
    private final ProgressDAO  progDAO  = new ProgressDAO();

    public String create(StudyPlan p) {
        if (p.getPlanName() == null || p.getPlanName().isBlank())
            return "Tên kế hoạch không được để trống.";
        if (p.getStartDate() == null || p.getEndDate() == null)
            return "Ngày bắt đầu và kết thúc không được để trống.";
        if (p.getStartDate().compareTo(p.getEndDate()) > 0)
            return "Ngày kết thúc phải sau ngày bắt đầu.";
            
        // UR-16: Tên kế hoạch không được trùng lặp
        if (planDAO.existsName(p.getUserId(), p.getPlanName(), 0))
            return "Tên kế hoạch '" + p.getPlanName() + "' đã tồn tại.";

        if (!planDAO.insert(p)) return "Lỗi hệ thống khi tạo kế hoạch.";
        // Tạo record progress 1-1 tương ứng
        progDAO.init(p.getPlanId());
        return null;
    }

    public String update(StudyPlan p) {
        if (p.getPlanName() == null || p.getPlanName().isBlank())
            return "Tên kế hoạch không được để trống.";
        if (p.getStartDate().compareTo(p.getEndDate()) > 0)
            return "Ngày kết thúc phải sau ngày bắt đầu.";
            
        // Kiểm tra trùng tên (trừ chính nó)
        if (planDAO.existsName(p.getUserId(), p.getPlanName(), p.getPlanId()))
            return "Tên kế hoạch '" + p.getPlanName() + "' đã tồn tại.";

        return planDAO.update(p) ? null : "Lỗi hệ thống khi cập nhật kế hoạch.";
    }

    public boolean delete(int planId, int userId) {
        return planDAO.delete(planId, userId);
    }

    public StudyPlan findById(int planId, int userId) {
        return planDAO.findById(planId, userId);
    }

    public List<StudyPlan> findByUser(int userId) {
        return planDAO.findByUser(userId);
    }
}
