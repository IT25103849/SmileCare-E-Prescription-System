package com.smilecare.controller;

import com.smilecare.entity.AppUser;
import com.smilecare.service.CurrentUserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class GlobalControllerAdvice {

    private final CurrentUserService currentUserService;

    public GlobalControllerAdvice(
            CurrentUserService currentUserService) {

        this.currentUserService =
                currentUserService;
    }

    @ModelAttribute
    public void addGlobalUserInformation(
            Model model) {

        try {

            AppUser user =
                    currentUserService
                            .getCurrentUser();

            model.addAttribute(
                    "currentUsername",
                    user.getUsername()
            );

            model.addAttribute(
                    "currentRole",
                    user.getRole()
            );

            model.addAttribute(
                    "isDentist",
                    "DENTIST".equalsIgnoreCase(
                            user.getRole()
                    )
            );

            model.addAttribute(
                    "isPatient",
                    "PATIENT".equalsIgnoreCase(
                            user.getRole()
                    )
            );

            model.addAttribute(
                    "isReceptionist",
                    "RECEPTIONIST".equalsIgnoreCase(
                            user.getRole()
                    )
            );

        } catch (Exception exception) {

            model.addAttribute(
                    "currentUsername",
                    null
            );

            model.addAttribute(
                    "currentRole",
                    null
            );

            model.addAttribute(
                    "isDentist",
                    false
            );

            model.addAttribute(
                    "isPatient",
                    false
            );

            model.addAttribute(
                    "isReceptionist",
                    false
            );
        }
    }
}