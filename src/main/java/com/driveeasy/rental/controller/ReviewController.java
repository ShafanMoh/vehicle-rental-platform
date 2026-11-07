package com.driveeasy.rental.controller;

import com.driveeasy.rental.model.review.Review;
import com.driveeasy.rental.service.ReviewService;
import com.driveeasy.rental.service.VehicleService;
import com.driveeasy.rental.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Customer reviews: write (Create), edit (Update) and delete (Delete).
 * Reviews are read on each vehicle's page.
 */
@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final VehicleService vehicleService;

    public ReviewController(ReviewService reviewService, VehicleService vehicleService) {
        this.reviewService = reviewService;
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public String add(@RequestParam String vehicleId, @RequestParam(required = false) Integer rating,
                      @RequestParam String comment, HttpSession session, RedirectAttributes redirect) {
        Review review = reviewService.add(SessionUtil.getUserId(session), vehicleId, rating, comment);
        redirect.addFlashAttribute("flashSuccess", "Thank you! Your review was posted as \""
                + review.getBadgeLabel() + "\".");
        return "redirect:/vehicles/" + vehicleId + "#reviews";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, HttpSession session, Model model,
                           RedirectAttributes redirect) {
        Review review = reviewService.getById(id);
        if (!review.isWrittenBy(SessionUtil.getUserId(session))) {
            redirect.addFlashAttribute("flashError", "You can only edit your own reviews.");
            return "redirect:/vehicles/" + review.getVehicleId();
        }
        model.addAttribute("review", review);
        model.addAttribute("vehicle", vehicleService.getById(review.getVehicleId()));
        return "review/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable String id, @RequestParam(required = false) Integer rating,
                         @RequestParam String comment, HttpSession session, RedirectAttributes redirect) {
        Review review = reviewService.update(id, SessionUtil.getUserId(session), rating, comment);
        redirect.addFlashAttribute("flashSuccess", "Your review has been updated.");
        return "redirect:/vehicles/" + review.getVehicleId() + "#reviews";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, @RequestParam(required = false) String returnTo,
                         HttpSession session, RedirectAttributes redirect) {
        Review review = reviewService.getById(id);
        reviewService.delete(id, SessionUtil.getUserId(session));
        redirect.addFlashAttribute("flashSuccess", "The review has been deleted.");
        boolean safeReturn = returnTo != null && returnTo.startsWith("/") && !returnTo.startsWith("//");
        return "redirect:" + (safeReturn ? returnTo : "/vehicles/" + review.getVehicleId() + "#reviews");
    }
}
