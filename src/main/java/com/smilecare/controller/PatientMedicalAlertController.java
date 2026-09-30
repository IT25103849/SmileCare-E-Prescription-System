package com.smilecare.controller;

import com.smilecare.entity.PatientMedicalAlert;
import com.smilecare.service.PatientMedicalAlertService;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientMedicalAlertController {

    private final PatientMedicalAlertService alertService;

    public PatientMedicalAlertController(
            PatientMedicalAlertService alertService) {

        this.alertService = alertService;
    }

    @PreAuthorize("hasRole('DENTIST')")
    @GetMapping("/{patientId}/alerts")
    public List<PatientMedicalAlert> getAlerts(
            @PathVariable Long patientId) {

        return alertService
                .getActiveAlerts(patientId);
    }
}