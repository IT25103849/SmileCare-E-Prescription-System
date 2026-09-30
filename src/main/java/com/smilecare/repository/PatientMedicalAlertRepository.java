package com.smilecare.repository;

import com.smilecare.entity.PatientMedicalAlert;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientMedicalAlertRepository
        extends JpaRepository<PatientMedicalAlert, Long> {

    List<PatientMedicalAlert>
    findByPatientIdAndActiveTrueOrderByCreatedAtDesc(
            Long patientId
    );
}