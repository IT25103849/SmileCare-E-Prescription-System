package com.smilecare.service;

import com.smilecare.entity.PrescriptionAuditLog;
import com.smilecare.repository.PrescriptionAuditLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionAuditService {

    private final PrescriptionAuditLogRepository auditRepository;
    private final CurrentUserService currentUserService;

    public PrescriptionAuditService(
            PrescriptionAuditLogRepository auditRepository,
            CurrentUserService currentUserService) {

        this.auditRepository = auditRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public void record(
            Long prescriptionId,
            String action,
            String details) {

        PrescriptionAuditLog log =
                new PrescriptionAuditLog();

        log.setPrescriptionId(
                prescriptionId
        );

        log.setAction(
                action
        );

        log.setActorUsername(
                currentUserService.getUsername()
        );

        log.setDetails(
                details
        );

        auditRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionAuditLog> getLogs(
            Long prescriptionId) {

        return auditRepository
                .findByPrescriptionIdOrderByActionAtDesc(
                        prescriptionId
                );
    }
}