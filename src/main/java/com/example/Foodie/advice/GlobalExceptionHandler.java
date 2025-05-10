package com.example.Foodie.advice; // Or your chosen package

import com.example.Foodie.exception.InsufficientStockException;
import jakarta.persistence.EntityNotFoundException; // Example
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView; // Import ModelAndView
import org.springframework.web.servlet.NoHandlerFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handler for 404 specifically (if not handled by Spring Boot default via error/404.html)
    // This can be more explicit if you have spring.web.resources.add-mappings=false
    // and spring.mvc.throw-exception-if-no-handler-found=true
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        logger.warn("404 - Resource not found: {}", ex.getRequestURL());
        ModelAndView mav = new ModelAndView("error/404"); // Your custom 404.html
        model.addAttribute("errorMessage", "The page you requested could not be found.");
        model.addAttribute("path", ex.getRequestURL());
        return mav;
    }

    // Handle custom InsufficientStockException
    @ExceptionHandler(InsufficientStockException.class)
    public ModelAndView handleInsufficientStockException(InsufficientStockException ex, Model model) {
        logger.warn("Insufficient Stock: {}", ex.getMessage());
        // You might redirect back to cart or product page with a flash attribute,
        // or show a specific error page. For now, let's use a generic error display.
        ModelAndView mav = new ModelAndView("error/general-error"); // Create this page
        model.addAttribute("errorTitle", "Order Problem");
        model.addAttribute("errorMessage", ex.getMessage());
        return mav;
    }

    // Handle generic EntityNotFoundException (can be thrown by orElseThrow in services)
    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex, Model model) {
        logger.warn("Entity not found: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/general-error"); // Create this page
        model.addAttribute("errorTitle", "Resource Not Found");
        model.addAttribute("errorMessage", "The requested item could not be found. " + (ex.getMessage() != null ? ex.getMessage() : ""));
        return mav;
    }

    // Example: Handle IllegalArgumentException (often used for bad request parameters)
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/general-error");
        model.addAttribute("errorTitle", "Invalid Request");
        model.addAttribute("errorMessage", "There was a problem with your request: " + ex.getMessage());
        return mav;
    }


    // Catch-all for other unexpected exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllOtherExceptions(Exception ex, Model model) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex); // Log the full stack trace for unexpected errors
        ModelAndView mav = new ModelAndView("error/500"); // Your custom 500.html
        model.addAttribute("errorMessage", "An unexpected internal error occurred. Please try again later.");
        // For development, you might want to pass more details:
        // model.addAttribute("exception", ex.getClass().getSimpleName());
        // model.addAttribute("trace", getStackTraceAsString(ex));
        return mav;
    }

    // Helper to get stack trace as string (for development display on 500 page if needed)
    /*
    private String getStackTraceAsString(Exception ex) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
    */
}