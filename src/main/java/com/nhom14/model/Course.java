package com.nhom14.model;

public class Course {
    private int    courseId;
    private int    userId;
    private String courseName;
    private String courseCode;
    private String lecturer;
    private int    credits;
    private String semester;

    public Course() {}
    public Course(int userId, String courseName, String courseCode, String lecturer, int credits, String semester) {
        this.userId     = userId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.lecturer   = lecturer;
        this.credits    = credits;
        this.semester   = semester;
    }

    public int    getCourseId()             { return courseId; }
    public void   setCourseId(int v)        { courseId   = v; }
    public int    getUserId()               { return userId; }
    public void   setUserId(int v)          { userId     = v; }
    public String getCourseName()           { return courseName; }
    public void   setCourseName(String v)   { courseName = v; }
    public String getCourseCode()           { return courseCode; }
    public void   setCourseCode(String v)   { courseCode = v; }
    public String getLecturer()             { return lecturer; }
    public void   setLecturer(String v)     { lecturer   = v; }
    public int    getCredits()              { return credits; }
    public void   setCredits(int v)         { credits    = v; }
    public String getSemester()             { return semester; }
    public void   setSemester(String v)     { semester   = v; }
}
