package com.smilecare.controller;

import com.smilecare.service.CurrentUserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CurrentUserService currentUserService;

    public HomeController(
            CurrentUserService currentUserService) {

        this.currentUserService =
                currentUserService;
    }

    @GetMapping("/")
    public String home() {

        if (currentUserService.hasRole("PATIENT")) {

            return "redirect:/prescriptions/my";
        }

        return "redirect:/prescriptions";
    }
}