package com.driveeasy.rental.controller.admin;

import com.driveeasy.rental.service.ReviewService;
import com.driveeasy.rental.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Admin review moderation page. Deleting uses the shared /reviews/{id}/delete action.
 */
@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;
    private final VehicleService vehicleService;

    public AdminReviewController(ReviewService reviewService, VehicleService vehicleService) {
        this.reviewService = reviewService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer rating, @RequestParam(required = false) String type,
                       Model model) {
        model.addAttribute("reviews", reviewService.search(rating, type));
        model.addAttribute("vehicleMap", vehicleService.getVehicleMap());
        model.addAttribute("rating", rating);
        model.addAttribute("type", type);
        return "admin/reviews";
    }
}
