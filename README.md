# Study Reminder — Hệ thống nhắc nhở tiến trình học tập

> Đồ án môn Nhập môn Công nghệ Phần mềm — Nhóm 14

---

## Giới thiệu

**Study Reminder** là ứng dụng web giúp sinh viên quản lý và theo dõi tiến trình học tập cá nhân. Hệ thống cho phép người dùng lập kế hoạch học tập, quản lý môn học, theo dõi nhiệm vụ và nhận nhắc nhở tự động khi deadline sắp đến.

---

## Kiến trúc hệ thống

Hệ thống được xây dựng theo mô hình **MVC mở rộng** (Model - View - Controller + Service + DAO):

```
View (Thymeleaf HTML)
    ↕
Controller (Spring MVC)
    ↕
Service (Business Logic)
    ↕
DAO (Data Access Object)
    ↕
Model (Java POJO)
    ↕
Database (SQLite)
```

---

## Công nghệ sử dụng

| Thành phần | Công nghệ | Phiên bản |
|---|---|---|
| Ngôn ngữ | Java | 17 (LTS) |
| Framework | Spring Boot | 3.2.5 |
| View Engine | Thymeleaf | 3.x |
| Database | SQLite | 3.47.x |
| Build Tool | Maven | 3.9.x |
| Server | Apache Tomcat (nhúng) | 10.x |

---

## Cấu trúc project

```
StudyReminderBoot/
├── pom.xml                                      ← Maven dependencies
└── src/main/
    ├── java/com/nhom14/
    │   ├── App.java                             ← Entry point Spring Boot
    │   ├── controller/
    │   │   ├── AuthController.java              ← Login, Register, Logout, Profile
    │   │   └── DashboardController.java         ← Dashboard
    │   ├── model/
    │   │   ├── DatabaseConnection.java          ← Singleton SQLite + tạo schema
    │   │   └── User.java                        ← Model người dùng
    │   ├── dao/
    │   │   └── UserDAO.java                     ← CRUD bảng users
    │   └── service/
    │       └── AuthService.java                 ← Logic xác thực + hash SHA-256
    └── resources/
        ├── application.properties               ← Cấu hình Spring Boot
        ├── static/css/
        │   └── style.css                        ← CSS toàn bộ app
        └── templates/
            ├── dashboard.html                   ← Trang chính sau đăng nhập
            └── auth/
                ├── login.html                   ← Trang đăng nhập
                ├── register.html                ← Trang đăng ký
                └── profile.html                 ← Trang hồ sơ / đổi mật khẩu
```

---

## Cơ sở dữ liệu

File SQLite được tạo tự động tại thư mục gốc project với tên `study_reminder.db`.

### Sơ đồ các bảng

| Bảng | Mô tả |
|---|---|
| `users` | Tài khoản người dùng (Student / Admin) |
| `study_plans` | Kế hoạch học tập |
| `courses` | Môn học |
| `tasks` | Nhiệm vụ học tập |
| `reminders` | Nhắc nhở tự động |

### Quan hệ giữa các bảng
```
users (1) ──── (N) study_plans
users (1) ──── (N) courses
study_plans (1) ──── (N) tasks
courses (1) ──── (N) tasks
tasks (1) ──── (N) reminders
users (1) ──── (N) reminders
```

---

## Hướng dẫn cài đặt và chạy

### Yêu cầu môi trường
- **JDK 17+** — https://adoptium.net
- **Maven 3.9+** — https://maven.apache.org/download.cgi
- **VS Code** với extension **Extension Pack for Java** và **Spring Boot Extension Pack**

### Các bước chạy

**1. Clone repository**
```bash
git clone https://github.com/<your-username>/StudyReminderBoot.git
cd StudyReminderBoot
```

**2. Chạy ứng dụng**
```bash
mvn spring-boot:run
```

**3. Truy cập**

Mở trình duyệt và vào: http://localhost:8080

> **Lưu ý:** Đường dẫn thư mục project **không được chứa ký tự tiếng Việt hoặc khoảng trắng** để tránh lỗi Maven encoding.

---

## Tính năng hiện tại

### ✅ Module 1 — Quản lý người dùng
- [x] Đăng ký tài khoản
- [x] Đăng nhập / Đăng xuất
- [x] Đổi mật khẩu
- [x] Phân quyền Student / Admin
- [x] Mật khẩu được mã hóa SHA-256

### 🚧 Module 2 — Quản lý kế hoạch học tập
- [ ] Xem danh sách kế hoạch
- [ ] Tạo / Sửa / Xóa kế hoạch
- [ ] Thêm nhiệm vụ vào kế hoạch

### 🚧 Module 3 — Quản lý môn học và nhiệm vụ
- [ ] Quản lý môn học (CRUD)
- [ ] Quản lý nhiệm vụ (CRUD)
- [ ] Cập nhật trạng thái nhiệm vụ

### 🚧 Module 4 — Nhắc nhở và theo dõi tiến độ
- [ ] Dashboard tổng quan
- [ ] Danh sách nhắc nhở
- [ ] Gửi nhắc nhở tự động (SystemTimer)

### 🚧 Admin Panel
- [ ] Xem danh sách người dùng
- [ ] Khóa / Mở khóa tài khoản
- [ ] Xóa tài khoản

---

## 🔐 Bảo mật

- Mật khẩu được hash bằng **SHA-256** trước khi lưu vào database
- Session được quản lý bởi Spring (HttpSession)
- Tài khoản bị khóa không thể đăng nhập

---

## 📸 Giao diện

### Trang đăng nhập
![Login](docs/screenshots/login.png)

### Trang đăng ký
![Register](docs/screenshots/register.png)

### Dashboard
![Dashboard](docs/screenshots/dashboard.png)

---

## 📄 Tài liệu

- [Báo cáo đồ án](docs/BaoCao_Nhom14.docx)
- [Sơ đồ Use Case](docs/usecase_final.puml)

---

## 📝 License

Đồ án học tập — Không sử dụng cho mục đích thương mại.
