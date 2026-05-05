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

        // 1. ROLES
        st.execute("""
            CREATE TABLE IF NOT EXISTS roles (
                role_id INTEGER PRIMARY KEY AUTOINCREMENT,
                role_name TEXT UNIQUE NOT NULL
            )
        """);

        st.execute("INSERT OR IGNORE INTO roles (role_name) VALUES ('STUDENT')");
        st.execute("INSERT OR IGNORE INTO roles (role_name) VALUES ('ADMIN')");

        // 2. USERS
        st.execute("""
            CREATE TABLE IF NOT EXISTS users (
                user_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name  TEXT NOT NULL,
                email      TEXT NOT NULL UNIQUE,
                password   TEXT NOT NULL,
                role       TEXT NOT NULL DEFAULT 'STUDENT',
                status     TEXT NOT NULL DEFAULT 'ACTIVE',
                created_at TEXT DEFAULT (datetime('now'))
            )
        """);

        // 3. PASSWORD_RESET_TOKENS
        st.execute("""
            CREATE TABLE IF NOT EXISTS password_reset_tokens (
                token_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                token TEXT NOT NULL,
                expiry_date DATETIME NOT NULL,
                FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
            )
        """);

        // 4. STUDY_PLANS
        st.execute("""
            CREATE TABLE IF NOT EXISTS study_plans (
                plan_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id    INTEGER NOT NULL,
                plan_name  TEXT NOT NULL,
                start_date TEXT NOT NULL,
                end_date   TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
            )
        """);

        // 5. COURSES
        st.execute("""
            CREATE TABLE IF NOT EXISTS courses (
                course_id   INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id     INTEGER NOT NULL,
                course_name TEXT NOT NULL,
                course_code TEXT,
                credits     INTEGER DEFAULT 0,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
            )
        """);

        // 6. TASKS
        st.execute("""
            CREATE TABLE IF NOT EXISTS tasks (
                task_id     INTEGER PRIMARY KEY AUTOINCREMENT,
                plan_id     INTEGER NOT NULL,
                course_id   INTEGER,
                task_name   TEXT NOT NULL,
                description TEXT,
                deadline    TEXT NOT NULL,
                priority    TEXT NOT NULL DEFAULT 'MEDIUM',
                status      TEXT NOT NULL DEFAULT 'TODO',
                FOREIGN KEY (plan_id)   REFERENCES study_plans(plan_id) ON DELETE CASCADE,
                FOREIGN KEY (course_id) REFERENCES courses(course_id)   ON DELETE SET NULL
            )
        """);

        // 7. PROGRESS
        st.execute("""
            CREATE TABLE IF NOT EXISTS progress (
                progress_id INTEGER PRIMARY KEY AUTOINCREMENT,
                plan_id INTEGER NOT NULL UNIQUE,
                total_tasks INTEGER DEFAULT 0,
                completed_tasks INTEGER DEFAULT 0,
                complete_rate REAL DEFAULT 0.0,
                FOREIGN KEY(plan_id) REFERENCES study_plans(plan_id) ON DELETE CASCADE
            )
        """);

        // 8. REMINDERS
        st.execute("""
            CREATE TABLE IF NOT EXISTS reminders (
                reminder_id INTEGER PRIMARY KEY AUTOINCREMENT,
                task_id     INTEGER NOT NULL,
                user_id     INTEGER NOT NULL,
                message     TEXT NOT NULL,
                sent_at     TEXT DEFAULT (datetime('now')),
                is_read     INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (task_id) REFERENCES tasks(task_id)   ON DELETE CASCADE,
                FOREIGN KEY (user_id) REFERENCES users(user_id)   ON DELETE CASCADE
            )
        """);

        st.close();
    }
}
