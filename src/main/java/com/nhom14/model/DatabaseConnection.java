package com.nhom14.model;

import java.sql.*;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:study_reminder.db";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON;");
            initSchema();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi kết nối SQLite: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }

    public Connection getConnection() { return connection; }

    private void initSchema() throws SQLException {
        Statement st = connection.createStatement();

        // Bảng 1: roles — Quản lý danh mục phân quyền (PK: roleId)
        st.execute("""
            CREATE TABLE IF NOT EXISTS roles (
                roleId    INTEGER PRIMARY KEY AUTOINCREMENT,
                roleName  TEXT NOT NULL UNIQUE
            )
        """);

        st.execute("INSERT OR IGNORE INTO roles(roleId, roleName) VALUES (1, 'STUDENT')");
        st.execute("INSERT OR IGNORE INTO roles(roleId, roleName) VALUES (2, 'ADMIN')");

        // Seed tài khoản Admin mặc định (Pass: admin123)
        st.execute("""
            INSERT OR IGNORE INTO users(fullName, email, password, roleId, status)
            VALUES ('Hệ thống Admin', 'admin@gmail.com', 'admin123', 2, 'ACTIVE')
        """);

        // Bảng 2: users — Tài khoản người dùng (PK: userId, FK: roleId)
        st.execute("""
            CREATE TABLE IF NOT EXISTS users (
                userId    INTEGER PRIMARY KEY AUTOINCREMENT,
                fullName  TEXT NOT NULL,
                studentId TEXT,
                username  TEXT,
                email     TEXT NOT NULL UNIQUE,
                password  TEXT NOT NULL,
                roleId    INTEGER NOT NULL DEFAULT 1,
                status    TEXT NOT NULL DEFAULT 'ACTIVE',
                createdAt TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (roleId) REFERENCES roles(roleId) ON DELETE RESTRICT
            )
        """);

        // Bảng 3: passwordResetTokens — Token khôi phục mật khẩu (PK: tokenId, FK: userId)
        st.execute("""
            CREATE TABLE IF NOT EXISTS passwordResetTokens (
                tokenId    INTEGER PRIMARY KEY AUTOINCREMENT,
                userId     INTEGER NOT NULL,
                token      TEXT NOT NULL UNIQUE,
                createdAt  TEXT DEFAULT (datetime('now')),
                expiryDate TEXT NOT NULL,
                FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE
            )
        """);

        // Bảng 4: studyPlans — Kế hoạch học tập (PK: planId, FK: userId)
        st.execute("""
            CREATE TABLE IF NOT EXISTS studyPlans (
                planId    INTEGER PRIMARY KEY AUTOINCREMENT,
                userId    INTEGER NOT NULL,
                courseId  INTEGER,
                planName  TEXT NOT NULL,
                startDate TEXT NOT NULL,
                endDate   TEXT NOT NULL,
                FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
                FOREIGN KEY (courseId) REFERENCES courses(courseId) ON DELETE SET NULL
            )
        """);

        // Bảng 5: courses — Môn học (PK: courseId, FK: userId)
        st.execute("""
            CREATE TABLE IF NOT EXISTS courses (
                courseId   INTEGER PRIMARY KEY AUTOINCREMENT,
                userId     INTEGER NOT NULL,
                courseName TEXT NOT NULL,
                courseCode TEXT,
                lecturer   TEXT,
                credits    INTEGER DEFAULT 0,
                semester   TEXT,
                FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE
            )
        """);

        // Bảng 6: tasks — Nhiệm vụ học tập (PK: taskId, FK: planId, courseId)
        st.execute("""
            CREATE TABLE IF NOT EXISTS tasks (
                taskId      INTEGER PRIMARY KEY AUTOINCREMENT,
                planId      INTEGER NOT NULL,
                courseId    INTEGER,
                taskName    TEXT NOT NULL,
                description TEXT,
                deadline    TEXT NOT NULL,
                priority    TEXT NOT NULL DEFAULT 'MEDIUM'
                                CHECK (priority IN ('LOW','MEDIUM','HIGH')),
                status      TEXT NOT NULL DEFAULT 'TODO'
                                CHECK (status IN ('TODO','IN_PROGRESS','DONE')),
                completedAt TEXT,
                FOREIGN KEY (planId)   REFERENCES studyPlans(planId)  ON DELETE CASCADE,
                FOREIGN KEY (courseId) REFERENCES courses(courseId)    ON DELETE SET NULL
            )
        """);

        // Bảng 7: reminders — Nhắc nhở (PK: reminderId, FK: taskId, userId)
        st.execute("""
            CREATE TABLE IF NOT EXISTS reminders (
                reminderId INTEGER PRIMARY KEY AUTOINCREMENT,
                taskId     INTEGER NOT NULL,
                userId     INTEGER NOT NULL,
                message    TEXT NOT NULL,
                sentAt     TEXT DEFAULT (datetime('now')),
                isRead     INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (taskId)  REFERENCES tasks(taskId)   ON DELETE CASCADE,
                FOREIGN KEY (userId)  REFERENCES users(userId)   ON DELETE CASCADE
            )
        """);

        // Bảng 8: progress — Tiến độ học tập (PK: progressId, FK: planId, quan hệ 1-1)
        st.execute("""
            CREATE TABLE IF NOT EXISTS progress (
                progressId   INTEGER PRIMARY KEY AUTOINCREMENT,
                planId       INTEGER NOT NULL UNIQUE,
                totalTask    INTEGER NOT NULL DEFAULT 0,
                completeTask INTEGER NOT NULL DEFAULT 0
                                 CHECK (completeTask >= 0),
                completeRate REAL NOT NULL DEFAULT 0
                                 CHECK (completeRate >= 0 AND completeRate <= 100),
                FOREIGN KEY (planId) REFERENCES studyPlans(planId) ON DELETE CASCADE
            )
        """);

        // Cập nhật schema (Migration)
        addColumnIfNotExists(st, "users", "studentId", "TEXT");
        addColumnIfNotExists(st, "users", "username", "TEXT");
        addColumnIfNotExists(st, "courses", "lecturer", "TEXT");
        addColumnIfNotExists(st, "studyPlans", "courseId", "INTEGER");

        st.close();
    }

    private void addColumnIfNotExists(Statement st, String table, String column, String type) {
        try {
            st.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
        } catch (SQLException e) {
            // Cột đã tồn tại hoặc lỗi khác, bỏ qua
        }
    }
}
