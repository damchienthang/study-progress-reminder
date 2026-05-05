package com.nhom14.model;

public class StudyPlan {
    private int    planId;
    private int    userId;
    private Integer courseId;
    private String planName;
    private String startDate;
    private String endDate;
    
    // Dữ liệu join (hiển thị)
    private String courseName;
    private double completeRate;
    private int    totalTask;
    private int    completeTask;

    public StudyPlan() {}
    public StudyPlan(int userId, String planName, String startDate, String endDate) {
        this.userId    = userId;
        this.planName  = planName;
        this.startDate = startDate;
        this.endDate   = endDate;
    }

    public int    getPlanId()               { return planId; }
    public void   setPlanId(int v)          { planId       = v; }
    public int    getUserId()               { return userId; }
    public void   setUserId(int v)          { userId       = v; }
    public Integer getCourseId()            { return courseId; }
    public void   setCourseId(Integer v)    { courseId     = v; }
    public String getPlanName()             { return planName; }
    public void   setPlanName(String v)     { planName     = v; }
    public String getStartDate()            { return startDate; }
    public void   setStartDate(String v)    { startDate    = v; }
    public String getEndDate()              { return endDate; }
    public void   setEndDate(String v)      { endDate      = v; }
    
    public String getCourseName()           { return courseName; }
    public void   setCourseName(String v)   { courseName   = v; }
    public double getCompleteRate()         { return completeRate; }
    public void   setCompleteRate(double v) { completeRate = v; }
    public int    getTotalTask()            { return totalTask; }
    public void   setTotalTask(int v)       { totalTask    = v; }
    public int    getCompleteTask()         { return completeTask; }
    public void   setCompleteTask(int v)    { completeTask = v; }
}
