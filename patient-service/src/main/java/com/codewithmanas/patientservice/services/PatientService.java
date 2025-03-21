package com.codewithmanas.patientservice.services;

import com.codewithmanas.patientservice.dtos.PatientResponseDTO;
import com.codewithmanas.patientservice.entities.Patient;
import com.codewithmanas.patientservice.mappers.PatientMapper;
import com.codewithmanas.patientservice.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients () {
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> patientResponseDTOs = patients.stream()
                .map(PatientMapper::toDTO).toList();

        //List<PatientResponseDTO> patientResponseDTOs = patients.stream()
        //.map(patient -> PatientMapper.toDTO(patient)).toList();

        return patientResponseDTOs;
    }
}
