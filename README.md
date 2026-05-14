# 📚 Study Reminder — Hệ thống nhắc nhở tiến trình học tập

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![SQLite](https://img.shields.io/badge/SQLite-3.x-blue.svg)](https://www.sqlite.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> **Đồ án môn Nhập môn Công nghệ Phần mềm — Nhóm 14**
> Một ứng dụng Web hiện đại giúp sinh viên quản lý lộ trình học tập, theo dõi deadline và tối ưu hóa hiệu suất cá nhân với giao diện chuyên nghiệp.

---

## 🌟 Giới thiệu

**Study Reminder** là trợ lý học tập thông minh dành cho sinh viên. Hệ thống được thiết kế để giải quyết vấn đề quản lý thời gian và khối lượng công việc khổng lồ, giúp người dùng không bao giờ bỏ lỡ deadline thông qua hệ thống nhắc nhở tự động và bảng điều khiển (Dashboard) trực quan.

**Điểm nhấn đặc biệt:** Giao diện được thiết kế theo phong cách **Corporate Dashboard** với tông màu **Trắng & Đỏ** chủ đạo, mang lại cảm giác chuyên nghiệp, hiện đại và tập trung.

---

## 🚀 Tính năng nổi bật

### 🛠 1. Quản lý Học tập Toàn diện
*   **Môn học**: Lưu trữ thông tin môn học, số tín chỉ và giảng viên.
*   **Kế hoạch (Study Plan)**: Lập lộ trình học tập theo từng giai đoạn hoặc học kỳ.
*   **Nhiệm vụ (Tasks)**: Quản lý chi tiết từng bài tập, đồ án với mức độ ưu tiên và thời hạn cụ thể.

### 🔔 2. Hệ thống Nhắc nhở Thông minh
*   **Tự động quét Deadline**: Hệ thống chạy ngầm (`SystemTimerService`) liên tục kiểm tra các nhiệm vụ sắp đến hạn.
*   **Thông báo tức thời**: Gửi lời nhắc ngay khi nhiệm vụ còn dưới 24 giờ.
*   **Dashboard trực quan**: Thống kê tỉ lệ hoàn thành và liệt kê các nhiệm vụ cần chú ý ngay khi đăng nhập.

### 🎨 3. Trải nghiệm Người dùng (UX/UI) Cao cấp
*   **Theme Trắng & Đỏ**: Thiết kế hiện đại, độ tương phản cao, dễ nhìn.
*   **Responsive Sidebar**: Menu điều hướng bên trái có thể đóng/mở linh hoạt (Hamburger menu), giúp tối ưu không gian làm việc.
*   **State Persistence**: Ghi nhớ trạng thái đóng/mở của Menu khi chuyển trang.

### 🛡 4. Quản trị & Bảo mật
*   **Phân quyền (RBAC)**: Phân định rõ rệt vai trò giữa **Sinh viên** và **Quản trị viên**.
*   **Admin Panel**: Công cụ dành cho Admin để quản lý danh sách người dùng, khóa/mở khóa tài khoản và đặt lại mật khẩu.
*   **Bảo mật**: Mã hóa mật khẩu người dùng bằng thuật toán SHA-256.

---

## 🛠 Công nghệ sử dụng

| Thành phần | Công nghệ | Mô tả |
| :--- | :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.2.5 | Xử lý logic và bảo mật hệ thống |
| **Frontend** | Thymeleaf, Vanilla JS, CSS3 | Giao diện hiện đại, không dùng framework nặng |
| **Database** | SQLite | Cơ sở dữ liệu nhẹ, không cần cài đặt server phức tạp |
| **Service** | Spring Task Scheduling | Xử lý các tác vụ nhắc nhở chạy ngầm |

---

## 📁 Cấu trúc Thư mục Chính

```text
src/main/java/com/nhom14/
├── controller/          # Điều hướng và xử lý yêu cầu HTTP
├── dao/                 # Tầng truy cập cơ sở dữ liệu (JDBC)
├── model/               # Định nghĩa các đối tượng thực thể
├── service/             # Xử lý logic nghiệp vụ & Tác vụ ngầm
└── App.java             # Điểm khởi chạy ứng dụng (Main class)

src/main/resources/
├── static/css/          # CSS định nghĩa giao diện Trắng-Đỏ
├── templates/           # Giao diện HTML (Thymeleaf)
└── application.properties # Cấu hình hệ thống
```

---

## ⚙️ Hướng dẫn Cài đặt & Chạy ứng dụng

1.  **Yêu cầu**: Đảm bảo đã cài đặt **JDK 17** và **Maven**.
2.  **Khởi chạy**:
    ```bash
    mvn spring-boot:run
    ```
3.  **Truy cập**: Mở trình duyệt và vào địa chỉ: `http://localhost:8080`
4.  **Tài khoản thử nghiệm**:
    *   **Admin**: `admin@gmail.com` / `admin123`
    *   **Sinh viên**: Có thể tự đăng ký tài khoản mới ngay trên giao diện.

---

## 🛡 Bảo mật & Quy tắc Dữ liệu
*   Dữ liệu được lưu trữ tự động vào file `study_reminder.db` tại thư mục gốc khi ứng dụng chạy lần đầu.
*   Hệ thống tự động khởi tạo dữ liệu mẫu (Seeding) nếu cơ sở dữ liệu trống.

---
*Phát triển bởi Nhóm 14 - Học viện Công nghệ Bưu chính Viễn thông*
