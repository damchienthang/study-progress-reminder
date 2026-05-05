package com.nhom14.config;

import com.nhom14.dao.UserDAO;
import com.nhom14.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        
        // Ngăn chặn trình duyệt cache trang (giúp fix lỗi Back button sau khi logout)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Expires", "0"); // Proxies

        String uri = request.getRequestURI();
        
        // Các đường dẫn không cần login
        if (uri.equals("/") || uri.equals("/login") || uri.equals("/register") || uri.startsWith("/css/") || uri.startsWith("/js/")) {
            return true;
        }

        // Kiểm tra session
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            response.sendRedirect("/login?error=timeout");
            return false;
        }

        // Kiểm tra trạng thái thực tế trong DB (Phòng trường hợp bị Admin khóa "nóng")
        User dbUser = userDAO.findById(sessionUser.getUserId());
        if (dbUser == null || dbUser.isLocked()) {
            session.invalidate();
            response.sendRedirect("/login?error=locked");
            return false;
        }

        return true;
    }
}
