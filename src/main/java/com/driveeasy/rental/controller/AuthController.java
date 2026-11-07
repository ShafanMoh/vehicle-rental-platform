package com.driveeasy.rental.controller;

import com.driveeasy.rental.dto.UserForm;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.model.user.Customer;
import com.driveeasy.rental.model.user.User;
import com.driveeasy.rental.service.UserService;
import com.driveeasy.rental.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Login, registration (Create user) and logout.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String next, Model model) {
        model.addAttribute("next", next);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        @RequestParam(required = false) String next, HttpSession session,
                        RedirectAttributes redirect, Model model) {
        try {
            User user = userService.login(username, password);
            SessionUtil.login(session, user.getId());
            redirect.addFlashAttribute("flashSuccess", "Welcome back, " + user.getFullName() + "!");
            return "redirect:" + (isSafeLocalPath(next) ? next : user.getHomePage());
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("next", next);
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new UserForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("form") UserForm form, HttpSession session,
                           RedirectAttributes redirect, Model model) {
        try {
            Customer customer = userService.register(form);
            SessionUtil.login(session, customer.getId());
            redirect.addFlashAttribute("flashSuccess",
                    "Your account is ready, " + customer.getFullName() + ". Pick a vehicle to get started!");
            return "redirect:/vehicles";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirect) {
        SessionUtil.logout(session);
        redirect.addFlashAttribute("flashSuccess", "You have been logged out.");
        return "redirect:/";
    }

    /** Only allow redirects to pages on this site (stops "open redirect" attacks). */
    private boolean isSafeLocalPath(String path) {
        return path != null && path.startsWith("/") && !path.startsWith("//") && !path.contains("\\");
    }
}
