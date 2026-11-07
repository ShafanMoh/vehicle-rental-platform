package com.driveeasy.rental.controller;

import com.driveeasy.rental.dto.UserForm;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.model.user.User;
import com.driveeasy.rental.service.RentalService;
import com.driveeasy.rental.service.ReviewService;
import com.driveeasy.rental.service.UserService;
import com.driveeasy.rental.service.VehicleService;
import com.driveeasy.rental.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * A logged-in user views (Read), edits (Update) or deletes (Delete) their own account.
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final RentalService rentalService;
    private final ReviewService reviewService;
    private final VehicleService vehicleService;

    public ProfileController(UserService userService, RentalService rentalService,
                             ReviewService reviewService, VehicleService vehicleService) {
        this.userService = userService;
        this.rentalService = rentalService;
        this.reviewService = reviewService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {
        User user = userService.getById(SessionUtil.getUserId(session));
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", UserForm.fromUser(user));
        }
        addPageData(user, model);
        return "user/profile";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute("form") UserForm form, HttpSession session,
                                RedirectAttributes redirect, Model model) {
        String userId = SessionUtil.getUserId(session);
        try {
            userService.updateProfile(userId, form);
            redirect.addFlashAttribute("flashSuccess", "Your profile has been updated.");
            return "redirect:/profile";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            addPageData(userService.getById(userId), model);
            return "user/profile";
        }
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam String currentPassword, @RequestParam String newPassword,
                                 @RequestParam String confirmPassword, HttpSession session,
                                 RedirectAttributes redirect) {
        userService.changePassword(SessionUtil.getUserId(session), currentPassword, newPassword, confirmPassword);
        redirect.addFlashAttribute("flashSuccess", "Your password has been changed.");
        return "redirect:/profile";
    }

    @PostMapping("/delete")
    public String deleteAccount(@RequestParam String password, HttpSession session, RedirectAttributes redirect) {
        String userId = SessionUtil.getUserId(session);
        if (!userService.getById(userId).authenticate(password)) {
            throw new BusinessException("Password is incorrect. Your account was not deleted.");
        }
        userService.delete(userId);
        SessionUtil.logout(session);
        redirect.addFlashAttribute("flashSuccess", "Your account has been deleted. We hope to see you again!");
        return "redirect:/";
    }

    private void addPageData(User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("rentalCount", rentalService.getByCustomer(user.getId()).size());
        model.addAttribute("myReviews", reviewService.getByCustomer(user.getId()));
        model.addAttribute("vehicleMap", vehicleService.getVehicleMap());
    }
}
