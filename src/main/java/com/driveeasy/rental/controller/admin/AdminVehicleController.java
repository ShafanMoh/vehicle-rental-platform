package com.driveeasy.rental.controller.admin;

import com.driveeasy.rental.dto.VehicleForm;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.model.vehicle.Vehicle;
import com.driveeasy.rental.model.vehicle.VehicleStatus;
import com.driveeasy.rental.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin fleet management: add (Create), list/search (Read), edit (Update), remove (Delete).
 */
@Controller
@RequestMapping("/admin/vehicles")
public class AdminVehicleController {

    private final VehicleService vehicleService;

    public AdminVehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, @RequestParam(required = false) String type,
                       @RequestParam(required = false) String status, Model model) {
        model.addAttribute("vehicles", vehicleService.search(q, type, null, false, null).stream()
                .filter(v -> status == null || status.isBlank() || v.getStatus().name().equals(status))
                .toList());
        model.addAttribute("statuses", VehicleStatus.values());
        model.addAttribute("q", q);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        return "admin/vehicles";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new VehicleForm());
        return showForm(model, null);
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("form") VehicleForm form, RedirectAttributes redirect, Model model) {
        try {
            Vehicle vehicle = vehicleService.create(form);
            redirect.addFlashAttribute("flashSuccess", vehicle.getDisplayName() + " was added as " + vehicle.getId() + ".");
            return "redirect:/admin/vehicles";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(model, null);
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        model.addAttribute("form", VehicleForm.fromVehicle(vehicleService.getById(id)));
        return showForm(model, id);
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable String id, @ModelAttribute("form") VehicleForm form,
                         RedirectAttributes redirect, Model model) {
        try {
            Vehicle vehicle = vehicleService.update(id, form);
            redirect.addFlashAttribute("flashSuccess", vehicle.getDisplayName() + " was updated.");
            return "redirect:/admin/vehicles";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(model, id);
        }
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable String id, @RequestParam VehicleStatus status,
                               RedirectAttributes redirect) {
        vehicleService.changeStatus(id, status);
        redirect.addFlashAttribute("flashSuccess", "Vehicle " + id + " is now " + status.getDisplayName() + ".");
        return "redirect:/admin/vehicles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, RedirectAttributes redirect) {
        String name = vehicleService.getById(id).getDisplayName();
        vehicleService.delete(id);
        redirect.addFlashAttribute("flashSuccess", name + " was removed from the fleet.");
        return "redirect:/admin/vehicles";
    }

    private String showForm(Model model, String vehicleId) {
        model.addAttribute("vehicleId", vehicleId);
        model.addAttribute("statuses", VehicleStatus.values());
        return "admin/vehicle-form";
    }
}
