package com.nhom14.model;

public class Progress {
    private int    progressId;
    private int    planId;
    private int    totalTask;
    private int    completeTask;
    private double completeRate;
    private String planName;    // join hiển thị

    public Progress() {}
    public Progress(int planId) {
        this.planId       = planId;
        this.totalTask    = 0;
        this.completeTask = 0;
        this.completeRate = 0.0;
    }

    public int    getProgressId()               { return progressId; }
    public void   setProgressId(int v)          { progressId    = v; }
    public int    getPlanId()                   { return planId; }
    public void   setPlanId(int v)              { planId        = v; }
    public int    getTotalTask()                { return totalTask; }
    public void   setTotalTask(int v)           { totalTask     = v; }
    public int    getCompleteTask()             { return completeTask; }
    public void   setCompleteTask(int v)        { completeTask  = v; }
    public double getCompleteRate()             { return completeRate; }
    public void   setCompleteRate(double v)     { completeRate  = v; }
    public String getPlanName()                 { return planName; }
    public void   setPlanName(String v)         { planName      = v; }

    // Tính toán lại tỷ lệ phần trăm
    public void recalculate() {
        completeRate = totalTask > 0
            ? Math.round((completeTask * 100.0 / totalTask) * 10.0) / 10.0
            : 0.0;
    }
}
