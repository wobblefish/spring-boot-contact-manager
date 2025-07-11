package com.mmcneil.contactmanager.controller;

import com.mmcneil.contactmanager.exception.EmailAlreadyExistsException;
import com.mmcneil.contactmanager.model.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.mmcneil.contactmanager.exception.UsernameAlreadyExistsException;
import com.mmcneil.contactmanager.service.UserService;

@Controller
public class RegistrationController {
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new RegistrationForm());
        return "register";
    }
    
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userForm") RegistrationForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("formAction", "/register");
            model.addAttribute("formMode", "create");
            return "register";
        }
        try {
            userService.registerUser(form);
            return "redirect:/login";
        } catch (UsernameAlreadyExistsException ex) {
            result.rejectValue("username", "error.userForm", "Username already exists");
            return "register";
        } catch (EmailAlreadyExistsException ex) {
            result.rejectValue("email", "error.userForm", "Email already exists");
            return "register";
        }
    }
}