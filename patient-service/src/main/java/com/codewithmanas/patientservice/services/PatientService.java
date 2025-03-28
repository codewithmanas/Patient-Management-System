package com.codewithmanas.patientservice.services;

import billing.BillingResponse;
import com.codewithmanas.patientservice.dtos.PatientRequestDTO;
import com.codewithmanas.patientservice.dtos.PatientResponseDTO;
import com.codewithmanas.patientservice.entities.Patient;
import com.codewithmanas.patientservice.exceptions.EmailAlreadyExistsException;
import com.codewithmanas.patientservice.exceptions.PatientNotFoundException;
import com.codewithmanas.patientservice.grpc.BillingServiceGrpcClient;
import com.codewithmanas.patientservice.kafka.KafkaProducer;
import com.codewithmanas.patientservice.mappers.PatientMapper;
import com.codewithmanas.patientservice.repositories.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients () {
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> patientResponseDTOs = patients.stream()
                .map(PatientMapper::toDTO).toList();

        //List<PatientResponseDTO> patientResponseDTOs = patients.stream()
        //.map(patient -> PatientMapper.toDTO(patient)).toList();

        return patientResponseDTOs;
    }


    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("---A patient with this email - " + patientRequestDTO.getEmail() + " already exists ");
        }

        Patient newPatient = patientRepository.save(PatientMapper.toEntity(patientRequestDTO));

        log.info("---New patient created on patient service: {}", newPatient);

        BillingResponse response = billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), newPatient.getName(), newPatient.getEmail());

        log.info("---Billing account created successfully via GPRC: {}", response);

         kafkaProducer.sendEvent(newPatient);

        // an email address must be unique
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {

        Patient patient  = patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient not found with ID: " + id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("---A patient with this email - "+ patientRequestDTO.getEmail() + " already exists ");
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDTO(updatedPatient);

    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
