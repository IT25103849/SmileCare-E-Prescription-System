package com.smilecare.repository;

import com.smilecare.entity.PrescriptionAuditLog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionAuditLogRepository
        extends JpaRepository<PrescriptionAuditLog, Long> {

    List<PrescriptionAuditLog>
    findByPrescriptionIdOrderByActionAtDesc(
            Long prescriptionId
    );
}