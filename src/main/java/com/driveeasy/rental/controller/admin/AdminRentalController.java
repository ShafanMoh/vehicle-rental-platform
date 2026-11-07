package com.driveeasy.rental.controller.admin;

import com.driveeasy.rental.model.payment.Payment;
import com.driveeasy.rental.model.payment.PaymentStatus;
import com.driveeasy.rental.model.rental.RentalStatus;
import com.driveeasy.rental.service.PaymentService;
import com.driveeasy.rental.service.RentalService;
import com.driveeasy.rental.service.UserService;
import com.driveeasy.rental.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Admin booking management: view all (Read), move through statuses (Update), remove old records (Delete).
 */
@Controller
@RequestMapping("/admin/rentals")
public class AdminRentalController {

    private final RentalService rentalService;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final PaymentService paymentService;

    public AdminRentalController(RentalService rentalService, VehicleService vehicleService,
                                 UserService userService, PaymentService paymentService) {
        this.rentalService = rentalService;
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String status, @RequestParam(required = false) String q,
                       Model model) {
        Map<String, Payment> paymentByRental = paymentService.getAll().stream()
                .filter(p -> p.getStatus() != PaymentStatus.REFUNDED)
                .collect(Collectors.toMap(Payment::getRentalId, Function.identity(), (a, b) -> a));
        model.addAttribute("rentals", rentalService.search(status, q));
        model.addAttribute("statuses", RentalStatus.values());
        model.addAttribute("vehicleMap", vehicleService.getVehicleMap());
        model.addAttribute("userMap", userService.getUserMap());
        model.addAttribute("paymentMap", paymentByRental);
        model.addAttribute("status", status);
        model.addAttribute("q", q);
        return "admin/rentals";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable String id, @RequestParam RentalStatus status,
                               RedirectAttributes redirect) {
        rentalService.updateStatus(id, status);
        redirect.addFlashAttribute("flashSuccess", "Booking " + id + " is now " + status.getDisplayName() + ".");
        return "redirect:/admin/rentals";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, RedirectAttributes redirect) {
        rentalService.deleteRecord(id);
        redirect.addFlashAttribute("flashSuccess", "Booking record " + id + " was deleted.");
        return "redirect:/admin/rentals";
    }
}
