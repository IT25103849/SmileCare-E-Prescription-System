package com.smilecare.service;

import com.smilecare.entity.AppUser;
import com.smilecare.entity.Prescription;
import com.smilecare.repository.AppUserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CurrentUserService {

    private final AppUserRepository appUserRepository;

    public CurrentUserService(
            AppUserRepository appUserRepository) {

        this.appUserRepository = appUserRepository;
    }

    public AppUser getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getName()
        )) {

            throw new AccessDeniedException(
                    "Authentication required."
            );
        }

        return appUserRepository
                .findByUsername(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "User not found."
                        ));
    }

    public String getUsername() {

        return getCurrentUser()
                .getUsername();
    }

    public boolean hasRole(
            String role) {

        return role.equalsIgnoreCase(
                getCurrentUser()
                        .getRole()
        );
    }

    public Long requireDentistId() {

        AppUser user =
                getCurrentUser();

        if (!"DENTIST".equalsIgnoreCase(
                user.getRole()
        )
                || user.getDentistId() == null) {

            throw new AccessDeniedException(
                    "Dentist account required."
            );
        }

        return user.getDentistId();
    }

    public Long requirePatientId() {

        AppUser user =
                getCurrentUser();

        if (!"PATIENT".equalsIgnoreCase(
                user.getRole()
        )
                || user.getPatientId() == null) {

            throw new AccessDeniedException(
                    "Patient account required."
            );
        }

        return user.getPatientId();
    }

    public void assertCanViewPrescription(
            Prescription prescription) {

        AppUser user =
                getCurrentUser();

        if ("DENTIST".equalsIgnoreCase(
                user.getRole()
        )
                || "RECEPTIONIST".equalsIgnoreCase(
                user.getRole()
        )) {

            return;
        }

        if ("PATIENT".equalsIgnoreCase(
                user.getRole()
        )
                && Objects.equals(
                user.getPatientId(),
                prescription.getPatientId()
        )) {

            return;
        }

        throw new AccessDeniedException(
                "You cannot access this prescription."
        );
    }
}