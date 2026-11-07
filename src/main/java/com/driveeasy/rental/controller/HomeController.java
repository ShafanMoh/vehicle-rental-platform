package com.driveeasy.rental.controller;

import com.driveeasy.rental.model.vehicle.VehicleStatus;
import com.driveeasy.rental.service.ReviewService;
import com.driveeasy.rental.service.UserService;
import com.driveeasy.rental.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home page and the About page.
 */
@Controller
public class HomeController {

    private static final int FEATURED_COUNT = 6;

    private final VehicleService vehicleService;
    private final ReviewService reviewService;
    private final UserService userService;

    public HomeController(VehicleService vehicleService, ReviewService reviewService, UserService userService) {
        this.vehicleService = vehicleService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featured", vehicleService.getFeatured(FEATURED_COUNT));
        model.addAttribute("ratings", reviewService.getAverageRatingMap());
        model.addAttribute("fleetByType", vehicleService.countByType());
        model.addAttribute("vehicleCount", vehicleService.getAll().size());
        model.addAttribute("availableCount", vehicleService.countByStatus(VehicleStatus.AVAILABLE));
        model.addAttribute("customerCount", userService.countCustomers());
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
