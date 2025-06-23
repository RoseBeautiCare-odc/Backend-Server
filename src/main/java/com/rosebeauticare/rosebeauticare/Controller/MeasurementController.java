package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.Model.Measurement;
import com.rosebeauticare.rosebeauticare.Service.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    @Autowired
    private MeasurementService measurementService;

    // Create a new measurement (admin or manager only)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> createMeasurement(@RequestBody Measurement measurement) {
        try {
            Measurement createdMeasurement = measurementService.createMeasurement(measurement);
            Map<String, Object> response = new HashMap<>();
            response.put("message",
                    "Successfully created measurement for customer: " + createdMeasurement.getCustomerId());
            response.put("data", createdMeasurement);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get measurement by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getMeasurementById(@PathVariable String id) {
        try {
            Measurement measurement = measurementService.getMeasurementById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved measurement: " + id);
            response.put("data", measurement);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }

    // Get measurements by customer ID
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getMeasurementsByCustomer(@PathVariable String customerId) {
        try {
            List<Measurement> measurements = measurementService.getMeasurementsByCustomerId(customerId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved measurements for customer: " + customerId);
            response.put("data", measurements);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }

    // Update a measurement
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> updateMeasurement(
            @PathVariable String id,
            @RequestBody Measurement updatedMeasurement) {
        try {
            Measurement measurement = measurementService.updateMeasurement(id, updatedMeasurement);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully updated measurement: " + id);
            response.put("data", measurement);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Delete a measurement
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> deleteMeasurement(@PathVariable String id) {
        try {
            measurementService.deleteMeasurement(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully deleted measurement: " + id);
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }
}