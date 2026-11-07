package com.driveeasy.rental.config;

import com.driveeasy.rental.model.user.User;
import com.driveeasy.rental.repository.UserRepository;
import com.driveeasy.rental.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Runs before protected pages. Sends visitors who are not logged in to the login page,
 * and blocks non-admins from the /admin pages.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    public AuthInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        HttpSession session = request.getSession();
        String userId = SessionUtil.getUserId(session);
        Optional<User> user = userId == null ? Optional.empty() : userRepository.findById(userId);

        if (user.isEmpty()) {
            String target = request.getRequestURI().substring(request.getContextPath().length());
            if (request.getQueryString() != null) {
                target += "?" + request.getQueryString();
            }
            String next = "GET".equals(request.getMethod()) ? target : "/";
            response.sendRedirect(request.getContextPath() + "/login?next="
                    + URLEncoder.encode(next, StandardCharsets.UTF_8));
            return false;
        }

        boolean adminPage = request.getRequestURI().startsWith(request.getContextPath() + "/admin");
        if (adminPage && !user.get().isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "This page is for administrators only.");
            return false;
        }
        return true;
    }
}
