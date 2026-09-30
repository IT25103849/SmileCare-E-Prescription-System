package com.smilecare.controller;

import com.smilecare.entity.Medicine;
import com.smilecare.repository.MedicineRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MedicineController {

    private final MedicineRepository medicineRepository;

    public MedicineController(
            MedicineRepository medicineRepository) {

        this.medicineRepository = medicineRepository;
    }

    @GetMapping("/medicines")
    public List<Medicine> getMedicines() {

        return medicineRepository
                .findByActiveTrueOrderByMedicineNameAsc();
    }
}