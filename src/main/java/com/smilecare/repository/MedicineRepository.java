package com.smilecare.repository;

import com.smilecare.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository
        extends JpaRepository<Medicine, Long> {

    List<Medicine> findByActiveTrueOrderByMedicineNameAsc();
}