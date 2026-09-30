package com.smilecare.service;

import com.smilecare.entity.PatientMedicalAlert;
import com.smilecare.repository.PatientMedicalAlertRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientMedicalAlertService {

    private final PatientMedicalAlertRepository alertRepository;

    public PatientMedicalAlertService(
            PatientMedicalAlertRepository alertRepository) {

        this.alertRepository = alertRepository;
    }

    @Transactional(readOnly = true)
    public List<PatientMedicalAlert> getActiveAlerts(
            Long patientId) {

        if (patientId == null) {

            return List.of();
        }

        return alertRepository
                .findByPatientIdAndActiveTrueOrderByCreatedAtDesc(
                        patientId
                );
    }
}