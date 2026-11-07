package com.driveeasy.rental.controller.admin;

import com.driveeasy.rental.model.payment.PaymentStatus;
import com.driveeasy.rental.service.PaymentService;
import com.driveeasy.rental.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin payment management: view all (Read), mark received or refund (Update).
 */
@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    public AdminPaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String status, @RequestParam(required = false) String method,
                       Model model) {
        model.addAttribute("payments", paymentService.search(status, method));
        model.addAttribute("userMap", userService.getUserMap());
        model.addAttribute("statuses", PaymentStatus.values());
        model.addAttribute("revenue", paymentService.getTotalRevenue());
        model.addAttribute("pendingCount", paymentService.countPending());
        model.addAttribute("status", status);
        model.addAttribute("method", method);
        return "admin/payments";
    }

    @PostMapping("/{id}/mark-paid")
    public String markPaid(@PathVariable String id, RedirectAttributes redirect) {
        paymentService.markPaid(id);
        redirect.addFlashAttribute("flashSuccess", "Payment " + id + " marked as paid.");
        return "redirect:/admin/payments";
    }

    @PostMapping("/{id}/refund")
    public String refund(@PathVariable String id, RedirectAttributes redirect) {
        paymentService.refund(id);
        redirect.addFlashAttribute("flashSuccess", "Payment " + id + " refunded and its booking cancelled.");
        return "redirect:/admin/payments";
    }
}
