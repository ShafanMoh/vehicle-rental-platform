package com.driveeasy.rental.controller.admin;

import com.driveeasy.rental.service.DashboardService;
import com.driveeasy.rental.service.RentalService;
import com.driveeasy.rental.service.UserService;
import com.driveeasy.rental.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.Map;

/**
 * Admin home page with key figures and recent bookings.
 */
@Controller
public class AdminDashboardController {

    private static final int RECENT_BOOKINGS = 6;

    private final DashboardService dashboardService;
    private final RentalService rentalService;
    private final VehicleService vehicleService;
    private final UserService userService;

    public AdminDashboardController(DashboardService dashboardService, RentalService rentalService,
                                    VehicleService vehicleService, UserService userService) {
        this.dashboardService = dashboardService;
        this.rentalService = rentalService;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        Map<String, Double> monthly = dashboardService.getMonthlyRevenue();
        double highestMonth = monthly.isEmpty() ? 0 : Collections.max(monthly.values());

        model.addAttribute("stats", dashboardService.getStats());
        model.addAttribute("monthlyRevenue", monthly);
        model.addAttribute("highestMonth", highestMonth == 0 ? 1 : highestMonth);
        model.addAttribute("fleetByType", vehicleService.countByType());
        model.addAttribute("recentRentals", rentalService.getRecent(RECENT_BOOKINGS));
        model.addAttribute("vehicleMap", vehicleService.getVehicleMap());
        model.addAttribute("userMap", userService.getUserMap());
        return "admin/dashboard";
    }
}
