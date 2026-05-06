# 📚 Study Reminder — Hệ thống nhắc nhở tiến trình học tập

> **Đồ án môn Nhập môn Công nghệ Phần mềm — Nhóm 14**
> Một ứng dụng Web hiện đại giúp sinh viên quản lý lộ trình học tập, theo dõi deadline và tối ưu hóa hiệu suất cá nhân.

---

## 🌟 Giới thiệu

**Study Reminder** được thiết kế để giải quyết vấn đề quản lý thời gian và khối lượng công việc khổng lồ của sinh viên. Hệ thống không chỉ là một danh sách công việc đơn thuần mà còn là một trợ lý học tập thông minh, tự động tính toán tiến độ và nhắc nhở người dùng qua Email và hệ thống thông báo nội bộ.

---

## 🛠 Công nghệ sử dụng

Hệ thống được xây dựng trên nền tảng Java hiện đại, đảm bảo tính ổn định và bảo mật cao:

| Thành phần | Công nghệ | Phiên bản | Mô tả |
|---|---|---|---|
| **Ngôn ngữ** | Java | 17 (LTS) | Nền tảng lập trình mạnh mẽ |
| **Framework** | Spring Boot | 3.2.5 | Core framework cho ứng dụng Web |
| **Giao diện** | Thymeleaf + CSS3 | 3.x | Template engine và giao diện hiện đại |
| **Cơ sở dữ liệu**| SQLite | 3.47.x | Lưu trữ dữ liệu local linh hoạt |
| **Bảo mật** | SHA-256 | - | Mã hóa mật khẩu an toàn |
| **Build Tool** | Maven | 3.9.x | Quản lý dự án và dependencies |

---

## 📁 Cấu trúc Project

Hệ thống tuân thủ nghiêm ngặt mô hình **MVC (Model-View-Controller)** mở rộng với tầng **Service** và **DAO**:

```text
StudyReminderBoot/
├── pom.xml                                      ← Maven dependencies
└── src/main/
    ├── java/com/nhom14/
    │   ├── App.java                             ← Entry point + Seeding Admin
    │   ├── controller/                          ← Xử lý luồng dữ liệu (HTTP Requests)
    │   │   ├── AdminController.java             ← Quản trị hệ thống & Người dùng
    │   │   ├── AuthController.java              ← Đăng ký, Đăng nhập, Profile
    │   │   ├── CourseController.java            ← Quản lý môn học
    │   │   ├── DashboardController.java         ← Tổng hợp dữ liệu & Thống kê
    │   │   ├── ReminderController.java          ← Quản lý thông báo
    │   │   ├── StudyPlanController.java         ← Quản lý kế hoạch học tập
    │   │   └── TaskController.java              ← Quản lý nhiệm vụ chi tiết
    │   ├── model/                               ← Định nghĩa các thực thể dữ liệu
    │   │   ├── Course.java, Task.java           ← Core objects
    │   │   ├── User.java, Role.java             ← Authentication objects
    │   │   └── DatabaseConnection.java          ← Singleton SQLite Connection
    │   ├── dao/                                 ← Tầng truy cập CSDL (JDBC)
    │   │   ├── CourseDAO.java, TaskDAO.java     ← CRUD cho học tập
    │   │   └── UserDAO.java, TokenDAO.java      ← CRUD cho người dùng
    │   └── service/                             ← Tầng xử lý Logic nghiệp vụ
    │       ├── AuthService.java                 ← Mã hóa & Xác thực
    │       ├── SystemTimerService.java          ← Background tasks (Cronjob)
    │       └── EmailService.java                ← Gửi thông báo qua Email
    └── resources/
        ├── application.properties               ← Cấu hình Spring Boot
        ├── static/css/style.css                 ← Giao diện Dark Mode hiện đại
        └── templates/                           ← View (Thymeleaf HTML)
            ├── auth/                            ← Giao diện người dùng
            ├── admin/                           ← Giao diện quản trị
            ├── plans/                           ← Giao diện kế hoạch & nhiệm vụ
            └── dashboard.html                   ← Trang chủ cá nhân hóa
```

---

## ✨ Tính năng chi tiết

### ✅ 1. Quản lý người dùng (Module 1)
- **Xác thực:** Đăng ký, Đăng nhập, Đăng xuất an toàn.
- **Bảo mật:** Mật khẩu được hash bằng SHA-256; hỗ trợ khôi phục mật khẩu qua Email.
- **Hồ sơ:** Cập nhật thông tin cá nhân và thay đổi mật khẩu.

### ✅ 2. Quản lý kế hoạch học tập (Module 2)
- **Lập kế hoạch:** Tạo các chiến dịch học tập theo giai đoạn (ví dụ: "Ôn thi cuối kỳ").
- **Tổ chức:** Liên kết kế hoạch với các môn học cụ thể.
- **Tiến độ:** Theo dõi tỉ lệ hoàn thành nhiệm vụ theo từng kế hoạch.

### ✅ 3. Quản lý môn học & Nhiệm vụ (Module 3)
- **Môn học:** Quản lý danh mục môn học, giảng viên, số tín chỉ.
- **Nhiệm vụ:** Tạo danh sách công việc (Task) với Deadline, Mô tả và Mức độ ưu tiên.
- **Trạng thái:** Chuyển đổi linh hoạt: *Chưa bắt đầu* ➔ *Đang làm* ➔ *Hoàn thành*.

### ✅ 4. Nhắc nhở & Thống kê (Module 4)
- **Dashboard:** Biểu đồ trực quan hóa dữ liệu, thẻ thống kê tổng quan.
- **Thông báo:** Tự động gửi cảnh báo khi nhiệm vụ sắp đến hạn (< 24 giờ).
- **Background Task:** `SystemTimerService` chạy ngầm để đảm bảo không bỏ lỡ bất kỳ deadline nào.

### ✅ 5. Quản trị hệ thống (Admin Panel)
- **Giám sát:** Xem danh sách toàn bộ sinh viên trong hệ thống.
- **Kiểm soát:** Khóa/Mở khóa tài khoản; Xóa người dùng vi phạm.
- **Hỗ trợ:** Đặt lại mật khẩu cho sinh viên khi cần thiết.

---

## 🔑 Tài khoản thử nghiệm

Sau khi khởi chạy ứng dụng, bạn có thể sử dụng tài khoản Admin mặc định:
- **Email:** `admin@gmail.com`
- **Mật khẩu:** `admin123`

---

## 🚀 Hướng dẫn cài đặt

1. **Yêu cầu:** Cài đặt JDK 17 và Maven.
2. **Clone:** `git clone <repository-url>`
3. **Chạy:** Mở terminal tại thư mục gốc và gõ:
   ```bash
   mvn spring-boot:run
   ```
4. **Truy cập:** Mở trình duyệt tại địa chỉ [http://localhost:8080](http://localhost:8080)

---

## 🛡 Bảo mật & Quy tắc
- Hệ thống tự động tạo file `study_reminder.db` tại thư mục gốc.
- Mọi thay đổi dữ liệu đều được kiểm tra phân quyền (User chỉ thấy dữ liệu của chính mình).
- Email nhắc nhở được gửi tự động (Cần cấu hình SMTP trong `application.properties` nếu muốn sử dụng thực tế).

---
*Phát triển bởi Nhóm 14 - K58 CNTT1*
