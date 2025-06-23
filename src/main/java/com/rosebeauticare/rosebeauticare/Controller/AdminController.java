package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.DTO.AddStaffResponseDTO;
import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Service.StaffService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private StaffService staffService;

    private String calculateAge(String dateofbirth) {
        if (dateofbirth == null || dateofbirth.isEmpty()) {
            return null;
        }
        try {
            LocalDate dob = LocalDate.parse(dateofbirth);
            LocalDate today = LocalDate.now();
            return String.valueOf(Period.between(dob, today).getYears());
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format for dateofbirth: {}", dateofbirth, e);
            throw new IllegalArgumentException("Invalid date format: " + dateofbirth);
        }
    }

    @PostMapping(value = "/staff", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addStaff(
            @RequestPart(value = "name") @NotBlank(message = "Name is required") String name,
            @RequestPart(value = "username") @NotBlank(message = "Username is required") String username,
            @RequestPart(value = "phonenumber") @NotBlank(message = "Phone number is required") String phonenumber,
            @RequestPart(value = "alternatephonenumber") String alternatephonenumber,
            @RequestPart(value = "email") @NotBlank(message = "Email is required") String email,
            @RequestPart(value = "dateofbirth", required = false) String dateofbirth,
            @RequestPart(value = "sex") @NotBlank(message = "Sex is required") String sex,
            @RequestPart(value = "maritalstatus") @NotBlank(message = "Marital status is required") String maritalstatus,
            @RequestPart(value = "role") @NotBlank(message = "Role is required") String role,
            @RequestPart(value = "securitypin") @NotBlank(message = "Security pin is required") String securitypin,
            @RequestPart(value = "joineddate") @NotBlank(message = "Joined date is required") String joineddate,
            @RequestPart(value = "documentType") @NotBlank(message = "Document type is required") String documentType,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "documentphoto", required = false) MultipartFile documentphoto) {
        try {
            // Validate unique fields
            if (staffService.getStaffByUsername(username) != null) {
                logger.warn("Validation failed: Username already exists: {}", username);
                return ResponseEntity.badRequest().body("Username already exists");
            }
            if (staffService.getStaffByEmail(email) != null) {
                logger.warn("Validation failed: Email already exists: {}", email);
                return ResponseEntity.badRequest().body("Email already exists");
            }
            if (!phonenumber.startsWith("+91") || phonenumber.length() != 13) {
                logger.warn("Validation failed: Invalid phone number format: {}", phonenumber);
                return ResponseEntity.badRequest().body("Phone number must start with +91 and be 10 digits");
            }
            if (staffService.getStaffByPhoneNumber(phonenumber) != null) {
                logger.warn("Validation failed: Phone number already exists: {}", phonenumber);
                return ResponseEntity.badRequest().body("Phone number already exists");
            }
            String normalizedAlternatePhone = (alternatephonenumber == null || alternatephonenumber.isEmpty()) ? "+91"
                    : alternatephonenumber;
            if (!normalizedAlternatePhone.equals("+91")) {
                if (!normalizedAlternatePhone.startsWith("+91") || normalizedAlternatePhone.length() != 13) {
                    logger.warn("Validation failed: Invalid alternate phone number format: {}",
                            normalizedAlternatePhone);
                    return ResponseEntity.badRequest()
                            .body("Alternate phone number must start with +91 and be 10 digits");
                }
                if (staffService.getStaffByPhoneNumber(normalizedAlternatePhone) != null) {
                    logger.warn("Validation failed: Alternate phone number already exists: {}",
                            normalizedAlternatePhone);
                    return ResponseEntity.badRequest().body("Alternate phone number already exists");
                }
            }

            Staff staff = new Staff();
            staff.setName(name);
            staff.setUsername(username);
            staff.setPhonenumber(phonenumber);
            staff.setAlternatephonenumber(normalizedAlternatePhone.equals("+91") ? null : normalizedAlternatePhone);
            staff.setEmail(email);
            staff.setDateofbirth(dateofbirth);
            staff.setAge(calculateAge(dateofbirth));
            staff.setSex(sex);
            staff.setMaritalstatus(maritalstatus);
            staff.setAddress(address);
            staff.setRole(role);
            staff.setSecuritypin(securitypin);
            staff.setJoineddate(joineddate);
            staff.setDocumentType(documentType);

            Staff savedStaff = staffService.addStaff(staff, photo, documentphoto);
            logger.info("Staff added successfully: {}", savedStaff.getId());
            return ResponseEntity.ok(new AddStaffResponseDTO(savedStaff.getId(), "Staff added successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input while adding staff: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding staff: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }


    @PutMapping(value = "/staff/{id}", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStaff(
            @PathVariable String id,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "username", required = false) String username,
            @RequestPart(value = "phonenumber", required = false) String phonenumber,
            @RequestPart(value = "alternatephonenumber", required = false) String alternatephonenumber,
            @RequestPart(value = "email", required = false) String email,
            @RequestPart(value = "dateofbirth", required = false) String dateofbirth,
            @RequestPart(value = "sex", required = false) String sex,
            @RequestPart(value = "maritalstatus", required = false) String maritalstatus,
            @RequestPart(value = "role", required = false) String role,
            @RequestPart(value = "securitypin", required = false) String securitypin,
            @RequestPart(value = "joineddate", required = false) String joineddate,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart(value = "documentType", required = false) String documentType,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "documentphoto", required = false) MultipartFile documentphoto) {
        try {
            Staff existingStaff = staffService.getStaffById(id);
            if (existingStaff == null) {
                logger.warn("Staff not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }

            // Validate unique fields
            if (username != null && !username.equals(existingStaff.getUsername())) {
                if (staffService.getStaffByUsername(username) != null) {
                    logger.warn("Validation failed: Username already exists: {}", username);
                    return ResponseEntity.badRequest().body("Username already exists");
                }
            }
            if (email != null && !email.equals(existingStaff.getEmail())) {
                if (staffService.getStaffByEmail(email) != null) {
                    logger.warn("Validation failed: Email already exists: {}", email);
                    return ResponseEntity.badRequest().body("Email already exists");
                }
            }
            if (phonenumber != null && !phonenumber.equals(existingStaff.getPhonenumber())) {
                if (!phonenumber.startsWith("+91") || phonenumber.length() != 13) {
                    logger.warn("Validation failed: Invalid phone number format: {}", phonenumber);
                    return ResponseEntity.badRequest().body("Phone number must start with +91 and be 10 digits");
                }
                if (staffService.getStaffByPhoneNumber(phonenumber) != null) {
                    logger.warn("Validation failed: Phone number already exists: {}", phonenumber);
                    return ResponseEntity.badRequest().body("Phone number already exists");
                }
            }
            String normalizedAlternatePhone = alternatephonenumber != null ? alternatephonenumber
                    : existingStaff.getAlternatephonenumber();
            if (normalizedAlternatePhone != null
                    && !normalizedAlternatePhone.equals(existingStaff.getAlternatephonenumber())) {
                if (!normalizedAlternatePhone.isEmpty() && !normalizedAlternatePhone.equals("+91")) {
                    if (!normalizedAlternatePhone.startsWith("+91") || normalizedAlternatePhone.length() != 13) {
                        logger.warn("Validation failed: Invalid alternate phone number format: {}",
                                normalizedAlternatePhone);
                        return ResponseEntity.badRequest()
                                .body("Alternate phone number must start with +91 and be 10 digits");
                    }
                    if (staffService.getStaffByPhoneNumber(normalizedAlternatePhone) != null) {
                        logger.warn("Validation failed: Alternate phone number already exists: {}",
                                normalizedAlternatePhone);
                        return ResponseEntity.badRequest().body("Alternate phone number already exists");
                    }
                }
            }

            Staff updatedStaff = new Staff();
            updatedStaff.setName(name);
            updatedStaff.setUsername(username);
            updatedStaff.setPhonenumber(phonenumber);
            updatedStaff.setAlternatephonenumber(
                    normalizedAlternatePhone != null && normalizedAlternatePhone.equals("+91") ? null
                            : normalizedAlternatePhone);
            updatedStaff.setEmail(email);
            updatedStaff.setDateofbirth(dateofbirth);
            updatedStaff.setAge(dateofbirth != null ? calculateAge(dateofbirth) : existingStaff.getAge());
            updatedStaff.setSex(sex);
            updatedStaff.setMaritalstatus(maritalstatus);
            updatedStaff.setRole(role);
            updatedStaff.setSecuritypin(securitypin);
            updatedStaff.setJoineddate(joineddate);
            updatedStaff.setAddress(address);
            updatedStaff.setDocumentType(documentType);

            Staff savedStaff = staffService.updateStaff(id, updatedStaff, photo, documentphoto);
            logger.info("Staff updated successfully, ID: {}", id);
            return ResponseEntity.ok(new AddStaffResponseDTO(savedStaff.getId(), "Staff updated successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for updating staff ID: {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating staff ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }
}