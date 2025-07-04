package com.mmcneil.contactmanager.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContactNotFoundException.class)
    public String handleContactNotFound(ContactNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Contact Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom-error";
    }

    // Handle generic 404 (NoHandlerFoundException)
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Page Not Found");
        model.addAttribute("errorMessage", "The page you are looking for does not exist.");
        return "error/custom-error";
    }
}