package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.DTO.StaffDetailsDTO;
import com.rosebeauticare.rosebeauticare.DTO.StaffListDTO;
import com.rosebeauticare.rosebeauticare.DTO.StaffPhotoDTO;
import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Service.StaffService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private static final Logger logger = LoggerFactory.getLogger(StaffController.class);

    @Autowired
    private StaffService staffService;

    @GetMapping("/details")
    @Cacheable(value = "staffDetails", key = "#id")
    public ResponseEntity<?> getStaffDetails(@RequestParam("id") String id) {
        try {
            if (id == null || id.isEmpty()) {
                logger.warn("Missing or empty ID parameter for staff details");
                return ResponseEntity.badRequest().body("Staff ID is required");
            }

            // Validate ObjectId format
            try {
                new ObjectId(id);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid ObjectId format for staff ID: {}", id);
                return ResponseEntity.badRequest().body("Invalid staff ID format");
            }

            // Fetch staff details
            Staff staff = staffService.getStaffById(id);
            if (staff == null) {
                logger.warn("Staff not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }

            // Fetch photo and document photo from GridFS
            byte[] photo = staffService.getStaffPhoto(staff.getPhoto());
            if (photo == null && staff.getPhoto() != null && !staff.getPhoto().isEmpty()) {
                logger.warn("Photo not found in GridFS for staff ID: {}, photo ID: {}", id, staff.getPhoto());
            }

            String photoContentType = staffService.getContentType(staff.getPhoto());
            if (photoContentType == null && staff.getPhoto() != null && !staff.getPhoto().isEmpty()) {
                logger.warn("Content type not found for photo ID: {} of staff ID: {}", staff.getPhoto(), id);
            }

            byte[] documentphoto = staffService.getStaffPhoto(staff.getDocumentphoto());
            if (documentphoto == null && staff.getDocumentphoto() != null && !staff.getDocumentphoto().isEmpty()) {
                logger.warn("Document photo not found in GridFS for staff ID: {}, document photo ID: {}", id,
                        staff.getDocumentphoto());
            }

            String documentPhotoContentType = staffService.getContentType(staff.getDocumentphoto());
            if (documentPhotoContentType == null && staff.getDocumentphoto() != null
                    && !staff.getDocumentphoto().isEmpty()) {
                logger.warn("Content type not found for document photo ID: {} of staff ID: {}",
                        staff.getDocumentphoto(), id);
            }

            // Map to DTO
            StaffDetailsDTO staffDetailsDTO = new StaffDetailsDTO(
                    staff.getId(),
                    staff.getName(),
                    staff.getUsername(),
                    staff.getPhonenumber(),
                    staff.getAlternatephonenumber(),
                    staff.getEmail(),
                    staff.getDateofbirth(),
                    staff.getAge(),
                    staff.getSex(),
                    staff.getMaritalstatus(),
                    staff.getJoineddate(),
                    staff.getAddress(),
                    staff.getRole(),
                    staff.getDocumentType(),
                    photo,
                    photoContentType,
                    documentphoto,
                    documentPhotoContentType);

            logger.info("Fetched all details for staff ID: {}", id);
            return ResponseEntity.ok(staffDetailsDTO);
        } catch (IOException e) {
            logger.error("IO error fetching files from GridFS for staff ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching files");
        } catch (Exception e) {
            logger.error("Unexpected error fetching staff details for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    @GetMapping("/photo")
    @Cacheable(value = "staffPhotos", key = "#id")
    public ResponseEntity<?> getStaffPhoto(@RequestParam("id") String id) {
        try {
            if (id == null || id.isEmpty()) {
                logger.warn("Missing or empty ID parameter");
                return ResponseEntity.badRequest().body("Staff ID is required");
            }

            // Validate ObjectId format
            try {
                new ObjectId(id);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid ObjectId format for staff ID: {}", id);
                return ResponseEntity.badRequest().body("Invalid staff ID format");
            }

            // Fetch staff details
            Staff staff = staffService.getStaffById(id);
            if (staff == null) {
                logger.warn("Staff not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }

            // Fetch photo from GridFS
            byte[] photo = staffService.getStaffPhoto(staff.getPhoto());
            if (photo == null && staff.getPhoto() != null && !staff.getPhoto().isEmpty()) {
                logger.warn("Photo not found in GridFS for staff ID: {}, photo ID: {}", id, staff.getPhoto());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Photo not found in GridFS");
            }

            String photoContentType = staffService.getContentType(staff.getPhoto());
            if (photoContentType == null && staff.getPhoto() != null && !staff.getPhoto().isEmpty()) {
                logger.warn("Content type not found for photo ID: {} of staff ID: {}", staff.getPhoto(), id);
            }

            // Map to DTO
            StaffPhotoDTO staffPhotoDTO = new StaffPhotoDTO(
                    staff.getId(),
                    staff.getUsername(),
                    photo,
                    photoContentType
            );

            logger.info("Fetched photo and details for staff ID: {}", id);
            return ResponseEntity.ok(staffPhotoDTO);
        } catch (IOException e) {
            logger.error("IO error fetching photo from GridFS for staff ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching photo");
        } catch (Exception e) {
            logger.error("Unexpected error fetching staff photo for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }
     @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getAllStaff() {
        try {
            List<StaffListDTO> staffList = staffService.getAllStaff();
            logger.info("Fetched {} staff members", staffList.size());
            return ResponseEntity.ok(staffList);
        } catch (Exception e) {
            logger.error("Error fetching all staff members: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }
}