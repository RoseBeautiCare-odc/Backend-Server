package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.DTO.CustomerResponseDTO;
import com.rosebeauticare.rosebeauticare.Model.Customer;
import com.rosebeauticare.rosebeauticare.Service.CustomerService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@Validated
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    private void validateCustomerSince(String customerSince) {
        if (customerSince == null || customerSince.isEmpty()) {
            return;
        }
        try {
            LocalDate.parse(customerSince);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format for customerSince: {}", customerSince, e);
            throw new IllegalArgumentException("Invalid date format for customerSince: " + customerSince);
        }
    }

    @PostMapping(value = "", consumes = { "multipart/form-data" })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> addCustomer(
            @RequestPart(value = "name") @NotBlank(message = "Name is required") String name,
            @RequestPart(value = "phonenumber") @NotBlank(message = "Phone number is required") String phonenumber,
            @RequestPart(value = "alternatePhoneNumber", required = false) String alternatePhoneNumber,
            @RequestPart(value = "maritalstatus") @NotBlank(message = "Marital status is required") String maritalstatus,
            @RequestPart(value = "townOrVillage", required = false) String townOrVillage,
            @RequestPart(value = "district", required = false) String district,
            @RequestPart(value = "state", required = false) String state,
            @RequestPart(value = "gender") @NotBlank(message = "Gender is required") String gender,
            @RequestPart(value = "customerSince") @NotBlank(message = "Customer since date is required") String customerSince) {
        try {
            // Validate unique fields
            if (customerService.getCustomerByName(name).isPresent()) {
                logger.warn("Validation failed: Name already exists: {}", name);
                return ResponseEntity.badRequest().body("Name already exists");
            }
            if (!phonenumber.startsWith("+91") || phonenumber.length() != 13) {
                logger.warn("Validation failed: Invalid phone number format: {}", phonenumber);
                return ResponseEntity.badRequest().body("Phone number must start with +91 and be 10 digits");
            }
            if (customerService.getCustomerByPhonenumber(phonenumber).isPresent()) {
                logger.warn("Validation failed: Phone number already exists: {}", phonenumber);
                return ResponseEntity.badRequest().body("Phone number already exists");
            }
            String normalizedAlternatePhone = (alternatePhoneNumber == null || alternatePhoneNumber.isEmpty()) ? null : alternatePhoneNumber;
            if (normalizedAlternatePhone != null) {
                if (!normalizedAlternatePhone.startsWith("+91") || normalizedAlternatePhone.length() != 13) {
                    logger.warn("Validation failed: Invalid alternate phone number format: {}", normalizedAlternatePhone);
                    return ResponseEntity.badRequest().body("Alternate phone number must start with +91 and be 10 digits");
                }
                if (customerService.getCustomerByAlternatePhoneNumber(normalizedAlternatePhone).isPresent()) {
                    logger.warn("Validation failed: Alternate phone number already exists: {}", normalizedAlternatePhone);
                    return ResponseEntity.badRequest().body("Alternate phone number already exists");
                }
            }

            // Validate customerSince date
            validateCustomerSince(customerSince);

            Customer customer = new Customer();
            customer.setName(name);
            customer.setPhonenumber(phonenumber);
            customer.setAlternatePhoneNumber(normalizedAlternatePhone);
            customer.setMaritalstatus(maritalstatus);
            if (townOrVillage != null || district != null || state != null) {
                customer.setAddress(new Customer.Address(townOrVillage, district, state));
            }
            customer.setGender(gender);
            customer.setCustomerSince(customerSince);

            Customer savedCustomer = customerService.addCustomer(customer);
            logger.info("Customer added successfully: {}", savedCustomer.getId());
            return ResponseEntity.ok(new CustomerResponseDTO(savedCustomer.getId(), "Customer created successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input while adding customer: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding customer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> updateCustomer(
            @PathVariable String id,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "phonenumber", required = false) String phonenumber,
            @RequestPart(value = "alternatePhoneNumber", required = false) String alternatePhoneNumber,
            @RequestPart(value = "maritalstatus", required = false) String maritalstatus,
            @RequestPart(value = "townOrVillage", required = false) String townOrVillage,
            @RequestPart(value = "district", required = false) String district,
            @RequestPart(value = "state", required = false) String state,
            @RequestPart(value = "gender", required = false) String gender,
            @RequestPart(value = "customerSince", required = false) String customerSince) {
        try {
            Customer existingCustomer = customerService.getCustomerById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            // Validate unique fields
            if (name != null && !name.equals(existingCustomer.getName())) {
                if (customerService.getCustomerByName(name).isPresent()) {
                    logger.warn("Validation failed: Name already exists: {}", name);
                    return ResponseEntity.badRequest().body("Name already exists");
                }
            }
            if (phonenumber != null && !phonenumber.equals(existingCustomer.getPhonenumber())) {
                if (!phonenumber.startsWith("+91") || phonenumber.length() != 13) {
                    logger.warn("Validation failed: Invalid phone number format: {}", phonenumber);
                    return ResponseEntity.badRequest().body("Phone number must start with +91 and be 10 digits");
                }
                if (customerService.getCustomerByPhonenumber(phonenumber).isPresent()) {
                    logger.warn("Validation failed: Phone number already exists: {}", phonenumber);
                    return ResponseEntity.badRequest().body("Phone number already exists");
                }
            }
            String normalizedAlternatePhone = alternatePhoneNumber != null ? alternatePhoneNumber : existingCustomer.getAlternatePhoneNumber();
            if (normalizedAlternatePhone != null && !normalizedAlternatePhone.equals(existingCustomer.getAlternatePhoneNumber())) {
                if (!normalizedAlternatePhone.isEmpty()) {
                    if (!normalizedAlternatePhone.startsWith("+91") || normalizedAlternatePhone.length() != 13) {
                        logger.warn("Validation failed: Invalid alternate phone number format: {}", normalizedAlternatePhone);
                        return ResponseEntity.badRequest().body("Alternate phone number must start with +91 and be 10 digits");
                    }
                    if (customerService.getCustomerByAlternatePhoneNumber(normalizedAlternatePhone).isPresent()) {
                        logger.warn("Validation failed: Alternate phone number already exists: {}", normalizedAlternatePhone);
                        return ResponseEntity.badRequest().body("Alternate phone number already exists");
                    }
                }
            }

            // Validate customerSince date
            validateCustomerSince(customerSince);

            Customer updatedCustomer = new Customer();
            updatedCustomer.setName(name);
            updatedCustomer.setPhonenumber(phonenumber);
            updatedCustomer.setAlternatePhoneNumber(normalizedAlternatePhone != null && normalizedAlternatePhone.isEmpty() ? null : normalizedAlternatePhone);
            updatedCustomer.setMaritalstatus(maritalstatus);
            if (townOrVillage != null || district != null || state != null) {
                updatedCustomer.setAddress(new Customer.Address(townOrVillage, district, state));
            } else if (existingCustomer.getAddress() != null) {
                updatedCustomer.setAddress(existingCustomer.getAddress());
            }
            updatedCustomer.setGender(gender);
            updatedCustomer.setCustomerSince(customerSince);

            customerService.updateCustomer(id, updatedCustomer);
            logger.info("Customer updated successfully, ID: {}", id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Customer updated successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for updating customer ID: {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating customer ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        try {
            customerService.deleteCustomer(id);
            logger.info("Customer deleted successfully, ID: {}", id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Customer deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for deleting customer ID: {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting customer ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }



    @GetMapping(value = "/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            logger.info("Fetched {} customers", customers.size());
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            logger.error("Error fetching all customers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }


}