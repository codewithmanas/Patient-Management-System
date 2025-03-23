package com.codewithmanas.patientservice.controllers;

import com.codewithmanas.patientservice.dtos.PatientRequestDTO;
import com.codewithmanas.patientservice.dtos.PatientResponseDTO;
import com.codewithmanas.patientservice.dtos.validators.CreatePatientValidationGroup;
import com.codewithmanas.patientservice.services.PatientService;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {

        List<PatientResponseDTO> patients = patientService.getPatients();
        return ResponseEntity.ok().body(patients);
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patient =  patientService.createPatient(patientRequestDTO);

        return ResponseEntity.ok().body(patient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id, @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {

        PatientResponseDTO patient = patientService.updatePatient(id, patientRequestDTO);

        return ResponseEntity.ok().body(patient);
    }
}
