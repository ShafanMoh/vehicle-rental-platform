package com.driveeasy.rental.controller;

import com.driveeasy.rental.dto.PaymentForm;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.model.payment.Payment;
import com.driveeasy.rental.model.rental.Rental;
import com.driveeasy.rental.model.user.User;
import com.driveeasy.rental.service.PaymentService;
import com.driveeasy.rental.service.RentalService;
import com.driveeasy.rental.service.UserService;
import com.driveeasy.rental.service.VehicleService;
import com.driveeasy.rental.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Checkout (Create payment), payment history and receipts (Read).
 */
@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final RentalService rentalService;
    private final VehicleService vehicleService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, RentalService rentalService,
                             VehicleService vehicleService, UserService userService) {
        this.paymentService = paymentService;
        this.rentalService = rentalService;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @GetMapping
    public String history(HttpSession session, Model model) {
        String customerId = SessionUtil.getUserId(session);
        model.addAttribute("payments", paymentService.getByCustomer(customerId));
        model.addAttribute("rentalMap", rentalService.getByCustomer(customerId).stream()
                .collect(Collectors.toMap(Rental::getId, Function.identity())));
        model.addAttribute("vehicleMap", vehicleService.getVehicleMap());
        return "payment/history";
    }

    @GetMapping("/checkout/{rentalId}")
    public String checkout(@PathVariable String rentalId, HttpSession session, Model model,
                           RedirectAttributes redirect) {
        Rental rental = rentalService.getForCustomer(rentalId, SessionUtil.getUserId(session));
        if (!rental.isAwaitingPayment()) {
            redirect.addFlashAttribute("flashError", "Booking " + rentalId + " does not need a payment.");
            return "redirect:/rentals";
        }
        addCheckoutData(rental, model);
        model.addAttribute("form", new PaymentForm());
        return "payment/checkout";
    }

    @PostMapping("/checkout/{rentalId}")
    public String pay(@PathVariable String rentalId, @ModelAttribute("form") PaymentForm form,
                      HttpSession session, RedirectAttributes redirect, Model model) {
        String customerId = SessionUtil.getUserId(session);
        try {
            Payment payment = paymentService.pay(rentalId, customerId, form);
            redirect.addFlashAttribute("flashSuccess", "Payment recorded. Your booking is confirmed!");
            return "redirect:/payments/" + payment.getId();
        } catch (BusinessException e) {
            // Never send the card number or CVV back to the browser.
            form.setCardNumber(null);
            form.setCvv(null);
            model.addAttribute("error", e.getMessage());
            addCheckoutData(rentalService.getForCustomer(rentalId, customerId), model);
            return "payment/checkout";
        }
    }

    /** Receipt page. Customers see their own receipts; admins can see any. */
    @GetMapping("/{id}")
    public String receipt(@PathVariable String id, HttpSession session, Model model) {
        User user = userService.getById(SessionUtil.getUserId(session));
        Payment payment = user.isAdmin() ? paymentService.getById(id) : paymentService.getForCustomer(id, user.getId());
        Rental rental = rentalService.getById(payment.getRentalId());
        model.addAttribute("payment", payment);
        model.addAttribute("rental", rental);
        model.addAttribute("vehicle", vehicleService.getVehicleMap().get(rental.getVehicleId()));
        model.addAttribute("customer", userService.getUserMap().get(payment.getCustomerId()));
        return "payment/receipt";
    }

    private void addCheckoutData(Rental rental, Model model) {
        model.addAttribute("rental", rental);
        model.addAttribute("vehicle", vehicleService.getById(rental.getVehicleId()));
    }
}
