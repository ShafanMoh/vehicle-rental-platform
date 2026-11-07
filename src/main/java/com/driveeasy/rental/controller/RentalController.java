package com.driveeasy.rental.controller;

import com.driveeasy.rental.model.payment.Payment;
import com.driveeasy.rental.model.payment.PaymentStatus;
import com.driveeasy.rental.model.rental.Rental;
import com.driveeasy.rental.service.PaymentService;
import com.driveeasy.rental.service.RentalService;
import com.driveeasy.rental.service.VehicleService;
import com.driveeasy.rental.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Customer bookings: book (Create), view (Read), change dates (Update), cancel (Delete).
 */
@Controller
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final VehicleService vehicleService;
    private final PaymentService paymentService;

    public RentalController(RentalService rentalService, VehicleService vehicleService,
                            PaymentService paymentService) {
        this.rentalService = rentalService;
        this.vehicleService = vehicleService;
        this.paymentService = paymentService;
    }

    @PostMapping("/book")
    public String book(@RequestParam String vehicleId,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                       @RequestParam String pickupLocation, HttpSession session, RedirectAttributes redirect) {
        Rental rental = rentalService.book(SessionUtil.getUserId(session), vehicleId, startDate, endDate,
                pickupLocation);
        redirect.addFlashAttribute("flashSuccess", "Booking " + rental.getId()
                + " created. Complete the payment to confirm it.");
        return "redirect:/payments/checkout/" + rental.getId();
    }

    @GetMapping
    public String myRentals(HttpSession session, Model model) {
        String customerId = SessionUtil.getUserId(session);
        Map<String, Payment> paymentByRental = paymentService.getByCustomer(customerId).stream()
                .filter(p -> p.getStatus() != PaymentStatus.REFUNDED)
                .collect(Collectors.toMap(Payment::getRentalId, Function.identity(), (a, b) -> a));
        model.addAttribute("rentals", rentalService.getByCustomer(customerId));
        model.addAttribute("vehicleMap", vehicleService.getVehicleMap());
        model.addAttribute("paymentMap", paymentByRental);
        return "rental/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, HttpSession session, Model model,
                           RedirectAttributes redirect) {
        Rental rental = rentalService.getForCustomer(id, SessionUtil.getUserId(session));
        if (!rental.isEditable()) {
            redirect.addFlashAttribute("flashError", "Booking " + id + " can no longer be changed.");
            return "redirect:/rentals";
        }
        model.addAttribute("rental", rental);
        model.addAttribute("vehicle", vehicleService.getById(rental.getVehicleId()));
        model.addAttribute("payment", paymentService.findForRental(id).orElse(null));
        model.addAttribute("tomorrow", LocalDate.now().plusDays(1));
        return "rental/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable String id,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                         @RequestParam String pickupLocation, HttpSession session, RedirectAttributes redirect) {
        Rental rental = rentalService.changeBooking(id, SessionUtil.getUserId(session), startDate, endDate,
                pickupLocation);
        if (rental.isAwaitingPayment()) {
            redirect.addFlashAttribute("flashSuccess", "Booking updated. Please pay the new amount to confirm it.");
            return "redirect:/payments/checkout/" + id;
        }
        redirect.addFlashAttribute("flashSuccess", "Booking " + id + " has been updated.");
        return "redirect:/rentals";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable String id, HttpSession session, RedirectAttributes redirect) {
        rentalService.cancelByCustomer(id, SessionUtil.getUserId(session));
        redirect.addFlashAttribute("flashSuccess", "Booking " + id + " has been cancelled. Any payment will be refunded.");
        return "redirect:/rentals";
    }
}
