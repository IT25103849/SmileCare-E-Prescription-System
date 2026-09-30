package com.smilecare.integration;

import org.springframework.stereotype.Service;

@Service
public class StandaloneGroupReferenceValidator
        implements GroupReferenceValidator {

    @Override
    public void validatePatient(Long patientId) {

        if (patientId == null || patientId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid patient ID."
            );
        }
    }

    @Override
    public void validateVisit(Long visitId) {

        if (visitId != null && visitId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid visit ID."
            );
        }
    }
}