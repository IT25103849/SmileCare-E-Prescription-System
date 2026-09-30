package com.smilecare.integration;

public interface GroupReferenceValidator {

    void validatePatient(Long patientId);

    void validateVisit(Long visitId);
}