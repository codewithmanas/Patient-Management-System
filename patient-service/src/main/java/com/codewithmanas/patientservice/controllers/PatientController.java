package com.codewithmanas.patientservice.controllers;


import com.codewithmanas.patientservice.dtos.PatientRequestDTO;
import com.codewithmanas.patientservice.dtos.PatientResponseDTO;
import com.codewithmanas.patientservice.entities.Patient;
import com.codewithmanas.patientservice.services.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getPatients () {

        List<PatientResponseDTO> patients = patientService.getPatients();
        return ResponseEntity.ok().body(patients);
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patient =  patientService.createPatient(patientRequestDTO);

        return ResponseEntity.ok().body(patient);
    }
}
