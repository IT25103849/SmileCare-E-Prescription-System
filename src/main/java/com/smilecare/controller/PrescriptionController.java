package com.smilecare.controller;

import com.smilecare.dto.PrescriptionForm;
import com.smilecare.dto.PrescriptionItemForm;

import com.smilecare.entity.Prescription;

import com.smilecare.repository.MedicineRepository;

import com.smilecare.service.CurrentUserService;
import com.smilecare.service.PatientMedicalAlertService;
import com.smilecare.service.PrescriptionAuditService;
import com.smilecare.service.PrescriptionPdfService;
import com.smilecare.service.PrescriptionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    private final MedicineRepository medicineRepository;

    private final PrescriptionPdfService pdfService;

    private final PrescriptionAuditService auditService;

    private final CurrentUserService currentUserService;

    private final PatientMedicalAlertService alertService;


    public PrescriptionController(
            PrescriptionService prescriptionService,
            MedicineRepository medicineRepository,
            PrescriptionPdfService pdfService,
            PrescriptionAuditService auditService,
            CurrentUserService currentUserService,
            PatientMedicalAlertService alertService) {

        this.prescriptionService =
                prescriptionService;

        this.medicineRepository =
                medicineRepository;

        this.pdfService =
                pdfService;

        this.auditService =
                auditService;

        this.currentUserService =
                currentUserService;

        this.alertService =
                alertService;
    }


    // =========================================================
    // LIST
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('DENTIST','RECEPTIONIST')"
    )
    @GetMapping
    public String listPrescriptions(
            Model model) {

        model.addAttribute(
                "prescriptions",
                prescriptionService
                        .getAllPrescriptions()
        );

        model.addAttribute(
                "canCreate",
                currentUserService
                        .hasRole("DENTIST")
        );

        return "prescription/list";
    }


    // =========================================================
    // CREATE
    // =========================================================

    @PreAuthorize("hasRole('DENTIST')")
    @GetMapping("/create")
    public String createPage(
            Model model) {

        PrescriptionForm form =
                new PrescriptionForm();

        form.getItems().add(
                new PrescriptionItemForm()
        );

        loadFormData(
                model,
                form
        );

        return "prescription/create";
    }


    // =========================================================
    // SAVE
    // =========================================================

    @PreAuthorize("hasRole('DENTIST')")
    @PostMapping("/save")
    public String savePrescription(

            @Valid
            @ModelAttribute("prescriptionForm")
            PrescriptionForm form,

            BindingResult result,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            loadFormData(
                    model,
                    form
            );

            return "prescription/create";
        }


        try {

            Prescription saved =
                    prescriptionService
                            .createPrescription(
                                    form
                            );

            redirectAttributes
                    .addFlashAttribute(
                            "successMessage",
                            "Prescription saved successfully as a draft."
                    );

            return "redirect:/prescriptions/"
                    + saved.getId();

        } catch (RuntimeException exception) {

            result.reject(
                    "prescription.error",
                    exception.getMessage()
            );

            loadFormData(
                    model,
                    form
            );

            return "prescription/create";
        }
    }


    // =========================================================
    // VIEW
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('DENTIST','PATIENT','RECEPTIONIST')"
    )
    @GetMapping("/{id}")
    public String viewPrescription(
            @PathVariable Long id,
            Model model) {

        Prescription prescription =
                prescriptionService
                        .getPrescriptionForCurrentUser(
                                id
                        );

        model.addAttribute(
                "prescription",
                prescription
        );

        model.addAttribute(
                "canManage",
                currentUserService
                        .hasRole("DENTIST")
        );

        model.addAttribute(
                "canAudit",
                currentUserService
                        .hasRole("DENTIST")
        );

        model.addAttribute(
                "isPatient",
                currentUserService
                        .hasRole("PATIENT")
        );

        return "prescription/view";
    }


    // =========================================================
    // EDIT
    // =========================================================

    @PreAuthorize("hasRole('DENTIST')")
    @GetMapping("/{id}/edit")
    public String editPrescription(
            @PathVariable Long id,
            Model model) {

        PrescriptionForm form =
                prescriptionService
                        .getEditForm(id);

        model.addAttribute(
                "prescriptionId",
                id
        );

        loadFormData(
                model,
                form
        );

        return "prescription/edit";
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @PreAuthorize("hasRole('DENTIST')")
    @PostMapping("/{id}/update")
    public String updatePrescription(

            @PathVariable Long id,

            @Valid
            @ModelAttribute("prescriptionForm")
            PrescriptionForm form,

            BindingResult result,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            model.addAttribute(
                    "prescriptionId",
                    id
            );

            loadFormData(
                    model,
                    form
            );

            return "prescription/edit";
        }


        try {

            prescriptionService
                    .updateDraftPrescription(
                            id,
                            form
                    );

            redirectAttributes
                    .addFlashAttribute(
                            "successMessage",
                            "Draft prescription updated successfully."
                    );

            return "redirect:/prescriptions/"
                    + id;

        } catch (RuntimeException exception) {

            result.reject(
                    "prescription.error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "prescriptionId",
                    id
            );

            loadFormData(
                    model,
                    form
            );

            return "prescription/edit";
        }
    }


    // =========================================================
    // ISSUE
    // =========================================================

    @PreAuthorize("hasRole('DENTIST')")
    @PostMapping("/{id}/issue")
    public String issuePrescription(

            @PathVariable Long id,

            RedirectAttributes redirectAttributes) {

        prescriptionService
                .issuePrescription(id);

        redirectAttributes
                .addFlashAttribute(
                        "successMessage",
                        "Prescription issued successfully."
                );

        return "redirect:/prescriptions/"
                + id;
    }


    // =========================================================
    // CANCEL
    // =========================================================

    @PreAuthorize("hasRole('DENTIST')")
    @PostMapping("/{id}/cancel")
    public String cancelPrescription(

            @PathVariable Long id,

            RedirectAttributes redirectAttributes) {

        prescriptionService
                .cancelPrescription(id);

        redirectAttributes
                .addFlashAttribute(
                        "successMessage",
                        "Prescription cancelled successfully."
                );

        return "redirect:/prescriptions/"
                + id;
    }


    // =========================================================
    // PDF
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('DENTIST','PATIENT','RECEPTIONIST')"
    )
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Long id) {

        Prescription prescription =
                prescriptionService
                        .getPrescriptionForCurrentUser(
                                id
                        );

        byte[] pdf =
                pdfService
                        .generatePrescriptionPdf(
                                prescription
                        );

        prescriptionService
                .recordPdfDownload(
                        id
                );

        String filename =
                "SmileCare-Prescription-"
                        + id
                        + ".pdf";

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + filename
                                + "\""
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);
    }


    // =========================================================
    // PATIENT HISTORY - STAFF
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('DENTIST','RECEPTIONIST')"
    )
    @GetMapping("/patient/{patientId}")
    public String patientHistory(

            @PathVariable Long patientId,

            Model model) {

        model.addAttribute(
                "pageTitle",
                "Patient "
                        + patientId
                        + " - Prescription History"
        );

        model.addAttribute(
                "prescriptions",
                prescriptionService
                        .getPatientHistory(
                                patientId
                        )
        );

        model.addAttribute(
                "showBackToAll",
                true
        );

        return "prescription/history";
    }


    // =========================================================
    // PATIENT OWN HISTORY
    // =========================================================

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/my")
    public String myPrescriptionHistory(
            Model model) {

        model.addAttribute(
                "pageTitle",
                "My Prescription History"
        );

        model.addAttribute(
                "prescriptions",
                prescriptionService
                        .getMyPrescriptionHistory()
        );

        model.addAttribute(
                "showBackToAll",
                false
        );

        return "prescription/history";
    }


    // =========================================================
    // AUDIT LOG
    // =========================================================

    @PreAuthorize("hasRole('DENTIST')")
    @GetMapping("/{id}/audit")
    public String auditLog(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "prescription",
                prescriptionService
                        .getPrescriptionById(
                                id
                        )
        );

        model.addAttribute(
                "auditLogs",
                auditService
                        .getLogs(
                                id
                        )
        );

        return "prescription/audit";
    }


    // =========================================================
    // COMMON FORM DATA
    // =========================================================

    private void loadFormData(
            Model model,
            PrescriptionForm form) {

        model.addAttribute(
                "prescriptionForm",
                form
        );

        model.addAttribute(
                "medicines",
                medicineRepository
                        .findByActiveTrueOrderByMedicineNameAsc()
        );

        model.addAttribute(
                "patientAlerts",
                alertService
                        .getActiveAlerts(
                                form.getPatientId()
                        )
        );
    }
}