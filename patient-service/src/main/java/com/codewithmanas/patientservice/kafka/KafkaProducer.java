package com.codewithmanas.patientservice.kafka;

import com.codewithmanas.patientservice.entities.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        log.info("1---Kafka Producer Config: {}", kafkaTemplate.getProducerFactory().getConfigurationProperties());
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();

        log.info("2---Kafka Producer Config: {}", kafkaTemplate.getProducerFactory().getConfigurationProperties());



        try {

            log.info("3---Kafka Producer Config: {}", kafkaTemplate.getProducerFactory().getConfigurationProperties());

            kafkaTemplate.send("patient", event.toByteArray());

            log.info("---Successfully sent event to Kafka topic");

        } catch (Exception e) {
            // throw new RuntimeException(e);
            log.error("---Error sending event to Kafka topic: {}, {}", event, e.getMessage(), e);
        }
    }


}
