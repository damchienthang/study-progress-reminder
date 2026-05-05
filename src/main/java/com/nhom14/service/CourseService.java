package com.nhom14.service;

import com.nhom14.dao.CourseDAO;
import com.nhom14.model.Course;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseService {

    private final CourseDAO courseDAO = new CourseDAO();

    public String create(Course c) {
        if (c.getCourseName() == null || c.getCourseName().isBlank())
            return "Tên môn học không được để trống.";
        // Kiểm tra trùng mã môn
        if (c.getCourseCode() != null && !c.getCourseCode().isBlank()
                && courseDAO.existsCode(c.getUserId(), c.getCourseCode(), 0))
            return "Mã môn học đã tồn tại trong danh sách của bạn.";
        return courseDAO.insert(c) ? null : "Lỗi hệ thống khi thêm môn học.";
    }

    public String update(Course c) {
        if (c.getCourseName() == null || c.getCourseName().isBlank())
            return "Tên môn học không được để trống.";
        if (c.getCourseCode() != null && !c.getCourseCode().isBlank()
                && courseDAO.existsCode(c.getUserId(), c.getCourseCode(), c.getCourseId()))
            return "Mã môn học đã tồn tại trong danh sách của bạn.";
        return courseDAO.update(c) ? null : "Lỗi hệ thống khi cập nhật môn học.";
    }

    public boolean delete(int courseId, int userId) {
        // 1. Xóa các nhiệm vụ liên quan trước (Cascade Delete thủ công)
        new com.nhom14.dao.TaskDAO().deleteByCourse(courseId);
        // 2. Xóa môn học
        return courseDAO.delete(courseId, userId);
    }

    public Course findById(int courseId, int userId) {
        return courseDAO.findById(courseId, userId);
    }

    public List<Course> findByUser(int userId) {
        return courseDAO.findByUser(userId);
    }

    public List<Course> findByUserAndSemester(int userId, String semester) {
        return courseDAO.findByUserAndSemester(userId, semester);
    }
}
