package com.smilecare.repository;

import com.smilecare.entity.Prescription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    List<Prescription>
    findAllByOrderByPrescriptionDateDesc();

    List<Prescription>
    findByPatientIdOrderByPrescriptionDateDesc(
            Long patientId
    );

    List<Prescription>
    findByPatientIdAndStatusInOrderByPrescriptionDateDesc(
            Long patientId,
            Collection<String> statuses
    );

    @Query("""
            SELECT DISTINCT p
            FROM Prescription p
            LEFT JOIN FETCH p.items i
            LEFT JOIN FETCH i.medicine
            WHERE p.id = :id
            """)
    Optional<Prescription> findByIdWithItems(
            @Param("id") Long id
    );
}