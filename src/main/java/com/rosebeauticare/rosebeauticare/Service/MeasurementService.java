package com.rosebeauticare.rosebeauticare.Service;

import com.rosebeauticare.rosebeauticare.Model.Measurement;
import com.rosebeauticare.rosebeauticare.Repository.MeasurementRepository;
import com.rosebeauticare.rosebeauticare.Repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Service
public class MeasurementService {

    private static final Logger logger = LoggerFactory.getLogger(MeasurementService.class);
    private static final List<String> VALID_SERVICE_TYPES = Arrays.asList("Blouse", "Churidar");

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Measurement createMeasurement(Measurement measurement) {
        // Validate required fields
        if (measurement.getCustomerId() == null || measurement.getCustomerId().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (measurement.getServiceType() == null || !VALID_SERVICE_TYPES.contains(measurement.getServiceType())) {
            throw new IllegalArgumentException("Invalid service type. Allowed: " + VALID_SERVICE_TYPES);
        }
        if (measurement.getMeasurementDetails() == null || measurement.getMeasurementDetails().isEmpty()) {
            throw new IllegalArgumentException("Measurement details are required");
        }

        // Validate customer existence
        if (!customerRepository.existsById(measurement.getCustomerId())) {
            throw new IllegalArgumentException("Customer not found with ID: " + measurement.getCustomerId());
        }

        // Set timestamps
        measurement.setCreatedAt(LocalDateTime.now());
        measurement.setUpdatedAt(LocalDateTime.now());

        Measurement savedMeasurement = measurementRepository.save(measurement);
        logger.info("Created measurement with ID: {} for customer: {}", savedMeasurement.getId(),
                savedMeasurement.getCustomerId());
        return savedMeasurement;
    }

    public Measurement getMeasurementById(String id) {
        return measurementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Measurement not found with ID: " + id));
    }

    public List<Measurement> getMeasurementsByCustomerId(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        return measurementRepository.findByCustomerId(customerId);
    }

    public Measurement updateMeasurement(String id, Measurement updatedMeasurement) {
        Measurement existingMeasurement = measurementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Measurement not found with ID: " + id));

        // Update provided fields
        if (updatedMeasurement.getCustomerId() != null && !updatedMeasurement.getCustomerId().isEmpty()) {
            if (!customerRepository.existsById(updatedMeasurement.getCustomerId())) {
                throw new IllegalArgumentException("Customer not found with ID: " + updatedMeasurement.getCustomerId());
            }
            existingMeasurement.setCustomerId(updatedMeasurement.getCustomerId());
        }
        if (updatedMeasurement.getServiceType() != null
                && VALID_SERVICE_TYPES.contains(updatedMeasurement.getServiceType())) {
            existingMeasurement.setServiceType(updatedMeasurement.getServiceType());
        }
        if (updatedMeasurement.getMeasurementDetails() != null
                && !updatedMeasurement.getMeasurementDetails().isEmpty()) {
            existingMeasurement.setMeasurementDetails(updatedMeasurement.getMeasurementDetails());
        }

        // Update timestamp
        existingMeasurement.setUpdatedAt(LocalDateTime.now());

        Measurement savedMeasurement = measurementRepository.save(existingMeasurement);
        logger.info("Updated measurement with ID: {}", id);
        return savedMeasurement;
    }

    public void deleteMeasurement(String id) {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Measurement not found with ID: " + id));
        measurementRepository.delete(measurement);
        logger.info("Deleted measurement with ID: {}", id);
    }
}