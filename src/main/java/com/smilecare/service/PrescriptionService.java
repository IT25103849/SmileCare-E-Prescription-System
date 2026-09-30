package com.smilecare.service;

import com.smilecare.dto.PrescriptionForm;
import com.smilecare.dto.PrescriptionItemForm;

import com.smilecare.entity.Medicine;
import com.smilecare.entity.Prescription;
import com.smilecare.entity.PrescriptionItem;

import com.smilecare.integration.GroupReferenceValidator;

import com.smilecare.repository.MedicineRepository;
import com.smilecare.repository.PrescriptionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final CurrentUserService currentUserService;
    private final PrescriptionAuditService auditService;
    private final GroupReferenceValidator referenceValidator;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            MedicineRepository medicineRepository,
            CurrentUserService currentUserService,
            PrescriptionAuditService auditService,
            GroupReferenceValidator referenceValidator) {

        this.prescriptionRepository = prescriptionRepository;
        this.medicineRepository = medicineRepository;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
        this.referenceValidator = referenceValidator;
    }


    // =========================================================
    // CREATE PRESCRIPTION
    // =========================================================

    @Transactional
    public Prescription createPrescription(
            PrescriptionForm form) {

        validateForm(form);

        Long dentistId =
                currentUserService
                        .requireDentistId();

        Prescription prescription =
                new Prescription();

        prescription.setPatientId(
                form.getPatientId()
        );

        prescription.setDentistId(
                dentistId
        );

        prescription.setVisitId(
                form.getVisitId()
        );

        prescription.setNotes(
                form.getNotes()
        );

        prescription.setStatus(
                "DRAFT"
        );

        applyItems(
                prescription,
                form.getItems()
        );

        Prescription saved =
                prescriptionRepository
                        .save(prescription);

        auditService.record(
                saved.getId(),
                "CREATE",
                "Prescription created as DRAFT."
        );

        return saved;
    }


    // =========================================================
    // UPDATE DRAFT
    // =========================================================

    @Transactional
    public Prescription updateDraftPrescription(
            Long id,
            PrescriptionForm form) {

        validateForm(form);

        Prescription prescription =
                getPrescriptionById(id);

        if (!"DRAFT".equals(
                prescription.getStatus()
        )) {

            throw new IllegalStateException(
                    "Only DRAFT prescriptions can be edited."
            );
        }

        prescription.setPatientId(
                form.getPatientId()
        );

        prescription.setVisitId(
                form.getVisitId()
        );

        prescription.setNotes(
                form.getNotes()
        );

        prescription.clearItems();

        applyItems(
                prescription,
                form.getItems()
        );

        Prescription saved =
                prescriptionRepository
                        .save(prescription);

        auditService.record(
                id,
                "UPDATE",
                "Draft prescription updated."
        );

        return saved;
    }


    // =========================================================
    // GET ALL PRESCRIPTIONS
    // =========================================================

    @Transactional(readOnly = true)
    public List<Prescription> getAllPrescriptions() {

        return prescriptionRepository
                .findAllByOrderByPrescriptionDateDesc();
    }


    // =========================================================
    // GET PRESCRIPTION BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public Prescription getPrescriptionById(
            Long id) {

        return prescriptionRepository
                .findByIdWithItems(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Prescription not found."
                        ));
    }


    // =========================================================
    // GET PRESCRIPTION FOR CURRENT USER
    // =========================================================

    @Transactional(readOnly = true)
    public Prescription getPrescriptionForCurrentUser(
            Long id) {

        Prescription prescription =
                getPrescriptionById(id);

        currentUserService
                .assertCanViewPrescription(
                        prescription
                );

        return prescription;
    }


    // =========================================================
    // PATIENT PRESCRIPTION HISTORY
    // =========================================================

    @Transactional(readOnly = true)
    public List<Prescription> getPatientHistory(
            Long patientId) {

        referenceValidator
                .validatePatient(patientId);

        return prescriptionRepository
                .findByPatientIdOrderByPrescriptionDateDesc(
                        patientId
                );
    }


    // =========================================================
    // LOGGED-IN PATIENT HISTORY
    // =========================================================

    @Transactional(readOnly = true)
    public List<Prescription> getMyPrescriptionHistory() {

        Long patientId =
                currentUserService
                        .requirePatientId();

        return prescriptionRepository
                .findByPatientIdAndStatusInOrderByPrescriptionDateDesc(
                        patientId,
                        List.of(
                                "ISSUED",
                                "CANCELLED"
                        )
                );
    }


    // =========================================================
    // LOAD DRAFT INTO EDIT FORM
    // =========================================================

    @Transactional(readOnly = true)
    public PrescriptionForm getEditForm(
            Long id) {

        Prescription prescription =
                getPrescriptionById(id);

        if (!"DRAFT".equals(
                prescription.getStatus()
        )) {

            throw new IllegalStateException(
                    "Only DRAFT prescriptions can be edited."
            );
        }

        PrescriptionForm form =
                new PrescriptionForm();

        form.setPatientId(
                prescription.getPatientId()
        );

        form.setVisitId(
                prescription.getVisitId()
        );

        form.setNotes(
                prescription.getNotes()
        );

        for (PrescriptionItem item
                : prescription.getItems()) {

            PrescriptionItemForm itemForm =
                    new PrescriptionItemForm();

            itemForm.setMedicineId(
                    item.getMedicine().getId()
            );

            itemForm.setDosage(
                    item.getDosage()
            );

            itemForm.setFrequency(
                    item.getFrequency()
            );

            itemForm.setDuration(
                    item.getDuration()
            );

            itemForm.setInstructions(
                    item.getInstructions()
            );

            form.getItems()
                    .add(itemForm);
        }

        if (form.getItems().isEmpty()) {

            form.getItems()
                    .add(
                            new PrescriptionItemForm()
                    );
        }

        return form;
    }


    // =========================================================
    // ISSUE PRESCRIPTION
    // =========================================================

    @Transactional
    public Prescription issuePrescription(
            Long id) {

        Prescription prescription =
                getPrescriptionById(id);

        if (!"DRAFT".equals(
                prescription.getStatus()
        )) {

            throw new IllegalStateException(
                    "Only DRAFT prescriptions can be issued."
            );
        }

        prescription.setStatus(
                "ISSUED"
        );

        Prescription saved =
                prescriptionRepository
                        .save(prescription);

        auditService.record(
                id,
                "ISSUE",
                "Prescription issued."
        );

        return saved;
    }


    // =========================================================
    // CANCEL PRESCRIPTION
    // =========================================================

    @Transactional
    public Prescription cancelPrescription(
            Long id) {

        Prescription prescription =
                getPrescriptionById(id);

        if ("CANCELLED".equals(
                prescription.getStatus()
        )) {

            throw new IllegalStateException(
                    "Prescription already cancelled."
            );
        }

        prescription.setStatus(
                "CANCELLED"
        );

        Prescription saved =
                prescriptionRepository
                        .save(prescription);

        auditService.record(
                id,
                "CANCEL",
                "Prescription cancelled."
        );

        return saved;
    }


    // =========================================================
    // PDF DOWNLOAD AUDIT
    // =========================================================

    @Transactional
    public void recordPdfDownload(
            Long id) {

        Prescription prescription =
                getPrescriptionForCurrentUser(id);

        if (!"ISSUED".equals(
                prescription.getStatus()
        )) {

            throw new IllegalStateException(
                    "Only ISSUED prescriptions can be downloaded."
            );
        }

        auditService.record(
                id,
                "PDF_DOWNLOAD",
                "Prescription PDF downloaded."
        );
    }


    // =========================================================
    // VALIDATE FORM
    // =========================================================

    private void validateForm(
            PrescriptionForm form) {

        if (form == null) {

            throw new IllegalArgumentException(
                    "Prescription form is required."
            );
        }

        if (form.getItems() == null
                || form.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one medicine is required."
            );
        }

        referenceValidator
                .validatePatient(
                        form.getPatientId()
                );

        referenceValidator
                .validateVisit(
                        form.getVisitId()
                );
    }


    // =========================================================
    // ADD MEDICINES TO PRESCRIPTION
    // =========================================================

    private void applyItems(
            Prescription prescription,
            List<PrescriptionItemForm> itemForms) {

        for (PrescriptionItemForm itemForm
                : itemForms) {

            if (itemForm.getMedicineId() == null) {

                throw new IllegalArgumentException(
                        "Please select a medicine."
                );
            }

            Medicine medicine =
                    medicineRepository
                            .findById(
                                    itemForm.getMedicineId()
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Medicine not found."
                                    ));

            if (!Boolean.TRUE.equals(
                    medicine.getActive()
            )) {

                throw new IllegalArgumentException(
                        "Medicine is inactive."
                );
            }

            PrescriptionItem item =
                    new PrescriptionItem();

            item.setMedicine(
                    medicine
            );

            item.setDosage(
                    itemForm.getDosage()
            );

            item.setFrequency(
                    itemForm.getFrequency()
            );

            item.setDuration(
                    itemForm.getDuration()
            );

            item.setInstructions(
                    itemForm.getInstructions()
            );

            prescription.addItem(
                    item
            );
        }
    }
}