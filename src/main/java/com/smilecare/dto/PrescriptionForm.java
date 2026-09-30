package com.smilecare.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionForm {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long visitId;

    private String notes;

    @Valid
    @NotEmpty(message = "At least one medicine is required")
    private List<PrescriptionItemForm> items = new ArrayList<>();

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getVisitId() {
        return visitId;
    }

    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PrescriptionItemForm> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItemForm> items) {
        this.items = items;
    }
}