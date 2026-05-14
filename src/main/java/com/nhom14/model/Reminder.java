package com.nhom14.model;

public class Reminder {
    private int    reminderId;
    private int    taskId;
    private int    planId;
    private int    userId;
    private String message;
    private String sentAt;
    private boolean isRead;
    private String taskName;   // join hiển thị

    public Reminder() {}
    public Reminder(int taskId, int userId, String message) {
        this.taskId  = taskId;
        this.userId  = userId;
        this.message = message;
        this.isRead  = false;
    }

    public int    getReminderId()            { return reminderId; }
    public void   setReminderId(int v)       { reminderId = v; }
    public int    getTaskId()                { return taskId; }
    public void   setTaskId(int v)           { taskId     = v; }
    public int    getUserId()                { return userId; }
    public void   setUserId(int v)           { userId     = v; }
    public int    getPlanId()                { return planId; }
    public void   setPlanId(int v)           { planId     = v; }
    public String getMessage()               { return message; }
    public void   setMessage(String v)       { message    = v; }
    public String getSentAt()                { return sentAt; }
    public void   setSentAt(String v)        { sentAt     = v; }
    public boolean isRead()                  { return isRead; }
    public void   setRead(boolean v)         { isRead     = v; }
    public String getTaskName()              { return taskName; }
    public void   setTaskName(String v)      { taskName   = v; }
}
