package com.driveeasy.rental.controller.admin;

import com.driveeasy.rental.dto.UserForm;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.model.user.User;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin user management: add (Create), search (Read), edit (Update), delete (Delete).
 */
@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final RentalService rentalService;
    private final VehicleService vehicleService;

    public AdminUserController(UserService userService, RentalService rentalService, VehicleService vehicleService) {
        this.userService = userService;
        this.rentalService = rentalService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, @RequestParam(required = false) String role,
                       Model model) {
        model.addAttribute("users", userService.search(q, role));
        model.addAttribute("q", q);
        model.addAttribute("role", role);
        return "admin/users";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new UserForm());
        return "admin/user-form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("form") UserForm form, RedirectAttributes redirect, Model model) {
        try {
            User user = userService.createUser(form);
            redirect.addFlashAttribute("flashSuccess", user.getFullName() + " was added as " + user.getId() + ".");
            return "redirect:/admin/users";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/user-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("form", UserForm.fromUser(user));
        addEditData(user, model);
        return "admin/user-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable String id, @ModelAttribute("form") UserForm form,
                         RedirectAttributes redirect, Model model) {
        try {
            User user = userService.updateByAdmin(id, form);
            redirect.addFlashAttribute("flashSuccess", user.getFullName() + "'s account was updated.");
            return "redirect:/admin/users";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            addEditData(userService.getById(id), model);
            return "admin/user-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, HttpSession session, RedirectAttributes redirect) {
        if (id.equals(SessionUtil.getUserId(session))) {
            throw new BusinessException("You cannot delete your own account from here. Use your profile page.");
        }
        String name = userService.getById(id).getFullName();
        userService.delete(id);
        redirect.addFlashAttribute("flashSuccess", name + "'s account was deleted.");
        return "redirect:/admin/users";
    }

    private void addEditData(User user, Model model) {
        model.addAttribute("editUser", user);
        model.addAttribute("userRentals", rentalService.getByCustomer(user.getId()));
        model.addAttribute("vehicleMap", vehicleService.getVehicleMap());
    }
}
