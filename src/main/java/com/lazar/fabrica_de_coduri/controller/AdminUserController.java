package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users-management")
public class AdminUserController {

    private AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", adminUserService.getAllUsers());
        return "users-management";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        adminUserService.getUserById(id).ifPresent(user -> model.addAttribute("user", user));
        return "users-management-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@ModelAttribute User user) {
        adminUserService.updateUser(user);
        return "redirect:/admin/users-management";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return "redirect:/admin/users-management";
    }
}
