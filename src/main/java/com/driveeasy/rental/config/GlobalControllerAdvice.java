package com.driveeasy.rental.config;

import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.exception.ResourceNotFoundException;
import com.driveeasy.rental.model.user.User;
import com.driveeasy.rental.repository.UserRepository;
import com.driveeasy.rental.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.net.URI;

/**
 * Applies to every controller:
 *  - makes the logged-in user available to every JSP as ${currentUser}
 *  - turns business errors into friendly messages
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserRepository userRepository;

    public GlobalControllerAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        String userId = SessionUtil.getUserId(session);
        return userId == null ? null : userRepository.findById(userId).orElse(null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException e, HttpServletResponse response, Model model) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("status", 404);
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    /**
     * A rule was broken after clicking a button (POST): go back to the same page and show the message.
     * Otherwise show the error page.
     */
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessError(BusinessException e, HttpServletRequest request, Model model) {
        String previousPage = samePagePath(request.getHeader("Referer"));
        if ("POST".equals(request.getMethod()) && previousPage != null) {
            RequestContextUtils.getOutputFlashMap(request).put("flashError", e.getMessage());
            return "redirect:" + previousPage;
        }
        model.addAttribute("status", 400);
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    /** Keeps only the path of the previous page, so we never redirect to another website. */
    private String samePagePath(String referer) {
        if (referer == null) {
            return null;
        }
        try {
            URI uri = URI.create(referer);
            String path = uri.getRawPath();
            if (path == null || path.isEmpty()) {
                return null;
            }
            return uri.getRawQuery() == null ? path : path + "?" + uri.getRawQuery();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
