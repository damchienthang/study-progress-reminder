package com.nhom14.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int    taskId;
    private int    planId;
    private int    courseId;
    private String courseName;   // join hiển thị
    private String taskName;
    private String description;
    private String deadline;
    private String priority;     // LOW | MEDIUM | HIGH
    private String status;       // TODO | IN_PROGRESS | DONE
    private String completedAt;

    public Task() {}
    public Task(int planId, int courseId, String taskName, String description,
                String deadline, String priority) {
        this.planId      = planId;
        this.courseId    = courseId;
        this.taskName    = taskName;
        this.description = description;
        this.deadline    = deadline;
        this.priority    = priority;
        this.status      = "TODO";
    }

    public int    getTaskId()               { return taskId; }
    public void   setTaskId(int v)          { taskId       = v; }
    public int    getPlanId()               { return planId; }
    public void   setPlanId(int v)          { planId       = v; }
    public int    getCourseId()             { return courseId; }
    public void   setCourseId(int v)        { courseId     = v; }
    public String getCourseName()           { return courseName; }
    public void   setCourseName(String v)   { courseName   = v; }
    public String getTaskName()             { return taskName; }
    public void   setTaskName(String v)     { taskName     = v; }
    public String getDescription()          { return description; }
    public void   setDescription(String v)  { description  = v; }
    public String getDeadline()             { return deadline; }
    public void   setDeadline(String v)     { deadline     = v; }
    public String getPriority()             { return priority; }
    public void   setPriority(String v)     { priority     = v; }
    public String getStatus()               { return status; }
    public void   setStatus(String v)       { status       = v; }
    public String getCompletedAt()          { return completedAt; }
    public void   setCompletedAt(String v)  { completedAt  = v; }

    // Kiểm tra nhiệm vụ sắp đến hạn (< 24 giờ)
    public boolean isUpcoming() {
        if ("DONE".equals(status) || deadline == null || deadline.isBlank()) return false;
        try {
            // Hỗ trợ cả định dạng "yyyy-MM-dd HH:mm:ss" và "yyyy-MM-ddTHH:mm"
            String cleanDeadline = deadline.replace("T", " ");
            if (cleanDeadline.length() == 16) cleanDeadline += ":00"; // Thêm giây nếu thiếu
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dl = LocalDateTime.parse(cleanDeadline, fmt);
            LocalDateTime now = LocalDateTime.now();
            
            return dl.isAfter(now) && dl.isBefore(now.plusHours(24));
        } catch (Exception e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // Kiểm tra nhiệm vụ quá hạn
    public boolean isOverdue() {
        if ("DONE".equals(status) || deadline == null || deadline.isBlank()) return false;
        try {
            String cleanDeadline = deadline.replace("T", " ");
            if (cleanDeadline.length() == 16) cleanDeadline += ":00";
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dl = LocalDateTime.parse(cleanDeadline, fmt);
            return LocalDateTime.now().isAfter(dl);
        } catch (Exception e) { return false; }
    }

    // Kiểm tra hoàn thành trễ
    public boolean isDoneLate() {
        if (!"DONE".equals(status) || completedAt == null || deadline == null) return false;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(completedAt, fmt).isAfter(LocalDateTime.parse(deadline, fmt));
        } catch (Exception e) { return false; }
    }
}
