package com.events.controller;

import com.events.entity.UserRegistrationDto;
import com.events.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    /* --- Inscription --- */

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("dto", new UserRegistrationDto());
        return "auth/register"; // -> /WEB-INF/views/auth/register.html
    }

    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("dto") UserRegistrationDto dto,
            BindingResult binding,
            Model model,
            RedirectAttributes flash) {

        if (binding.hasErrors()) return "auth/register";

        try {
            userService.register(dto);
            flash.addFlashAttribute("successKey", "register.success");
            return "redirect:/login";          // Pattern PRG
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorKey", ex.getMessage());
            return "auth/register";
        }
    }

    /* --- Connexion --- */

    @GetMapping("/login")
    public String showLogin(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        if (error  != null) model.addAttribute("errorKey",  "login.error");
        if (logout != null) model.addAttribute("logoutDone", true);
        return "auth/login"; // -> /WEB-INF/views/auth/login.html
    }
}
