# Study Progress Reminder — Hệ thống Nhắc nhở Tiến độ Học tập

Dự án quản lý học tập dành cho sinh viên, xây dựng trên nền tảng **Spring Boot 3** và **SQLite**, đáp ứng đầy đủ các yêu cầu về phân tích thiết kế hệ thống (ERD) và các testcase nghiệp vụ.

## 📁 Cấu trúc Project (Architecture)
Hệ thống tuân thủ kiến trúc MVC/BCE chuyên nghiệp:
*   `com.nhom14.controller`: Xử lý điều hướng và tiếp nhận yêu cầu từ người dùng.
*   `com.nhom14.service`: Chứa logic nghiệp vụ (Validation, Tính toán tiến độ, Xử lý ràng buộc).
*   `com.nhom14.dao`: Tương tác trực tiếp với Database (SQLite) bằng JDBC.
*   `com.nhom14.model`: Định nghĩa các thực thể (User, Task, Course, StudyPlan...) khớp 100% với ERD.
*   `com.nhom14.config`: Cấu hình hệ thống (Bảo mật, Interceptor).
*   `src/main/resources/templates`: Giao diện người dùng (Thymeleaf, CSS Vanilla).

## 🚀 Hướng dẫn khởi chạy
1. **Yêu cầu:** Đã cài đặt Java 17+ và Maven.
2. **Chạy server:**
   ```powershell
   mvn spring-boot:run
   ```
3. **Truy cập:** [http://localhost:8080](http://localhost:8080)

## 🔑 Tài khoản kiểm thử (Test Accounts)
| Vai trò | Email | Mật khẩu | Chức năng chính |
| :--- | :--- | :--- | :--- |
| **Quản trị viên** | `admin@gmail.com` | `admin123` | Quản lý người dùng, Khóa/Mở khóa tài khoản. |
| **Sinh viên** | `ta1@gmail.com` | `tando1879` | Quản lý môn học, kế hoạch, nhiệm vụ và tiến độ. |

## 🛠 Nhật ký thay đổi (Changelog - Các điểm đã hoàn thiện)

### ✅ Database & ERD Alignment
*   Đồng bộ toàn bộ thuộc tính bảng: Thêm `lecturer` (Môn học), `studentId`, `username` (Người dùng), `courseId` (Kế hoạch).
*   Thiết lập `PRAGMA foreign_keys = ON` và `ON DELETE CASCADE` để đảm bảo tính nhất quán (Xóa môn học xóa luôn Nhiệm vụ).

### ✅ Bảo mật (Security)
*   **SessionInterceptor:** Chặn truy cập trái phép, tự động đẩy người dùng bị Admin khóa "nóng" ra khỏi hệ thống.
*   **Fix Browser Cache:** Ngăn chặn việc nhấn nút "Back" trên trình duyệt có thể xem lại dữ liệu sau khi Logout.

### ✅ Tính năng Nghiệp vụ (Business Logic)
*   **Task Validation:** Chặn Deadline trong quá khứ và Deadline nằm ngoài khoảng thời gian của kế hoạch học tập.
*   **Real-time Filter:** Bổ sung ô tìm kiếm môn học ngay tại giao diện (phản hồi tức thì không cần load trang).
*   **Progress Calculation:** Tự động tính toán lại % tiến độ kế hoạch mỗi khi trạng thái nhiệm vụ thay đổi.

### ✅ Sửa lỗi (Bug Fixes)
*   Fix lỗi 500 khi đổi mật khẩu/cập nhật hồ sơ (thiếu Model attribute).
*   Fix lỗi 404 khi cập nhật trạng thái nhiệm vụ từ trang chi tiết kế hoạch.
*   Fix lỗi định dạng ngày tháng `datetime-local` không tương thích với SQLite.

---
*Dự án được hoàn thiện bởi Antigravity AI Assistant.*
