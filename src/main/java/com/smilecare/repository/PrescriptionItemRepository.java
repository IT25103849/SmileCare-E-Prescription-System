package com.smilecare.repository;

import com.smilecare.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionItemRepository
        extends JpaRepository<PrescriptionItem, Long> {
}