package com.example.Foodie.controller;

import com.example.Foodie.dto.UserRegistrationDto; // If using DTO
import com.example.Foodie.model.User;
import com.example.Foodie.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    private final UserService userService;

    @Autowired
    public MainController(UserService userService) { // Autowire UserService
        this.userService = userService;
    }

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto()); // Use DTO
        return "register"; // Path to register.html
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "Match.userDto.confirmPassword", "Passwords must match");
        }

        if (bindingResult.hasErrors()) {
            return "register"; // Return to form if validation errors
        }

        try {
            User userToRegister = new User(); // Create a User entity from DTO
            userToRegister.setUsername(userDto.getUsername());
            userToRegister.setEmail(userDto.getEmail());
            userToRegister.setPassword(userDto.getPassword()); // Password will be encoded in service

            userService.registerNewUser(userToRegister);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) { // Catch exceptions from service (e.g., username/email exists)
            // bindingResult.rejectValue(null, "RegistrationError", e.getMessage());
            model.addAttribute("registrationError", e.getMessage());
            return "register";
        }
    }
}