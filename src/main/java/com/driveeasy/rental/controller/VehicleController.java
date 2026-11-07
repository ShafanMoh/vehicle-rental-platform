package com.driveeasy.rental.controller;

import com.driveeasy.rental.dto.RentalQuote;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.model.user.Customer;
import com.driveeasy.rental.model.vehicle.Vehicle;
import com.driveeasy.rental.service.RentalService;
import com.driveeasy.rental.service.ReviewService;
import com.driveeasy.rental.service.UserService;
import com.driveeasy.rental.service.VehicleService;
import com.driveeasy.rental.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Public pages for browsing and searching the fleet (Read).
 */
@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final ReviewService reviewService;
    private final RentalService rentalService;
    private final UserService userService;

    public VehicleController(VehicleService vehicleService, ReviewService reviewService,
                             RentalService rentalService, UserService userService) {
        this.vehicleService = vehicleService;
        this.reviewService = reviewService;
        this.rentalService = rentalService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) Double maxRate,
                       @RequestParam(required = false) String sort,
                       @RequestParam(defaultValue = "false") boolean availableOnly,
                       Model model) {
        model.addAttribute("vehicles", vehicleService.search(q, type, maxRate, availableOnly, sort));
        model.addAttribute("ratings", reviewService.getAverageRatingMap());
        model.addAttribute("q", q);
        model.addAttribute("type", type);
        model.addAttribute("maxRate", maxRate);
        model.addAttribute("sort", sort);
        model.addAttribute("availableOnly", availableOnly);
        return "vehicle/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, HttpSession session, Model model) {
        Vehicle vehicle = vehicleService.getById(id);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("reviews", reviewService.getForVehicle(id));
        model.addAttribute("averageRating", reviewService.getAverageRating(id));
        model.addAttribute("bookedDates", rentalService.getUpcomingForVehicle(id));
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("tomorrow", LocalDate.now().plusDays(1));

        Customer customer = userService.findCustomerOrNull(SessionUtil.getUserId(session));
        if (customer != null) {
            model.addAttribute("myReview", reviewService.findByCustomerAndVehicle(customer.getId(), id).orElse(null));
        }
        return "vehicle/detail";
    }

    /** Price preview used by the booking form (returns JSON). */
    @GetMapping("/{id}/quote")
    @ResponseBody
    public Map<String, Object> quote(@PathVariable String id,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                     HttpSession session) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            Customer customer = userService.findCustomerOrNull(SessionUtil.getUserId(session));
            RentalQuote quote = rentalService.quote(vehicleService.getById(id), customer, start, end);
            result.put("days", quote.days());
            result.put("baseCost", quote.baseCost());
            result.put("discount", quote.discount());
            result.put("total", quote.total());
        } catch (BusinessException e) {
            result.put("error", e.getMessage());
        }
        return result;
    }
}
