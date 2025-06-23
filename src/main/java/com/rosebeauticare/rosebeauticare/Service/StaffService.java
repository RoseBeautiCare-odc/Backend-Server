package com.rosebeauticare.rosebeauticare.Service;

import com.rosebeauticare.rosebeauticare.DTO.StaffListDTO;
import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Repository.StaffRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class StaffService {

    private static final Logger logger = LoggerFactory.getLogger(StaffService.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_CONTENT_TYPES = { "image/jpeg", "image/png" };
    private static final String FALLBACK_CONTENT_TYPE = "application/octet-stream";

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GridFsService gridFsService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Transactional
    public Staff addStaff(Staff staff, MultipartFile photo, MultipartFile documentphoto) throws IOException {
        // Validate staff fields
        if (staff.getSecuritypin() == null || staff.getSecuritypin().length() < 6) {
            logger.warn("Validation failed: Security pin must be at least 6 characters");
            throw new IllegalArgumentException("Security pin must be at least 6 characters");
        }
        validateRole(staff.getRole());

        // Validate files
        validateFile(photo, "photo");
        validateFile(documentphoto, "documentphoto");

        staff.setSecuritypin(passwordEncoder.encode(staff.getSecuritypin()));
        Staff savedStaff = staffRepository.save(staff);
        String staffId = savedStaff.getId();

        if (photo != null && !photo.isEmpty()) {
            String fileId = gridFsService.storeFile(photo, staffId);
            logger.debug("Stored photo for staff ID: {}, file ID: {}, contentType: {}", staffId, fileId, photo.getContentType());
            savedStaff.setPhoto(fileId);
        } else {
            savedStaff.setPhoto(null);
        }
        if (documentphoto != null && !documentphoto.isEmpty()) {
            String documentFileId = gridFsService.storeFile(documentphoto, staffId);
            logger.debug("Stored document photo for staff ID: {}, file ID: {}, contentType: {}", staffId, documentFileId, documentphoto.getContentType());
            savedStaff.setDocumentphoto(documentFileId);
        } else {
            savedStaff.setDocumentphoto(null);
        }

        Staff updatedStaff = staffRepository.save(savedStaff);
        logger.info("Staff added successfully with ID: {}", staffId);
        return updatedStaff;
    }

    @Transactional
    public Staff updateStaff(String id, Staff updatedStaff, MultipartFile photo, MultipartFile documentphoto)
            throws IOException {
        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Staff not found with ID: {}", id);
                    return new IllegalArgumentException("Staff not found with id: " + id);
                });

        if (updatedStaff.getName() != null)
            existingStaff.setName(updatedStaff.getName());
        if (updatedStaff.getUsername() != null)
            existingStaff.setUsername(updatedStaff.getUsername());
        if (updatedStaff.getPhonenumber() != null)
            existingStaff.setPhonenumber(updatedStaff.getPhonenumber());
        if (updatedStaff.getAlternatephonenumber() != null)
            existingStaff.setAlternatephonenumber(updatedStaff.getAlternatephonenumber());
        if (updatedStaff.getEmail() != null)
            existingStaff.setEmail(updatedStaff.getEmail());
        if (updatedStaff.getDateofbirth() != null)
            existingStaff.setDateofbirth(updatedStaff.getDateofbirth());
        if (updatedStaff.getAge() != null)
            existingStaff.setAge(updatedStaff.getAge());
        if (updatedStaff.getSex() != null)
            existingStaff.setSex(updatedStaff.getSex());
        if (updatedStaff.getMaritalstatus() != null)
            existingStaff.setMaritalstatus(updatedStaff.getMaritalstatus());
        if (updatedStaff.getJoineddate() != null)
            existingStaff.setJoineddate(updatedStaff.getJoineddate());
        if (updatedStaff.getAddress() != null)
            existingStaff.setAddress(updatedStaff.getAddress());
        if (updatedStaff.getRole() != null) {
            validateRole(updatedStaff.getRole());
            existingStaff.setRole(updatedStaff.getRole());
        }
        if (updatedStaff.getSecuritypin() != null) {
            if (updatedStaff.getSecuritypin().length() < 6) {
                logger.warn("Validation failed: Security pin must be at least 6 characters for staff ID: {}", id);
                throw new IllegalArgumentException("Security pin must be at least 6 characters");
            }
            existingStaff.setSecuritypin(passwordEncoder.encode(updatedStaff.getSecuritypin()));
        }
        if (updatedStaff.getDocumentType() != null)
            existingStaff.setDocumentType(updatedStaff.getDocumentType());

        // Validate files
        validateFile(photo, "photo");
        validateFile(documentphoto, "documentphoto");

        if (photo != null && !photo.isEmpty()) {
            String fileId = gridFsService.storeFile(photo, id);
            logger.debug("Stored photo for staff ID: {}, file ID: {}, contentType: {}", id, fileId, photo.getContentType());
            existingStaff.setPhoto(fileId);
        }
        if (documentphoto != null && !documentphoto.isEmpty()) {
            String documentFileId = gridFsService.storeFile(documentphoto, id);
            logger.debug("Stored document photo for staff ID: {}, file ID: {}, contentType: {}", id, documentFileId, documentphoto.getContentType());
            existingStaff.setDocumentphoto(documentFileId);
        }

        Staff updated = staffRepository.save(existingStaff);
        logger.info("Staff updated successfully with ID: {}", id);
        return updated;
    }

    @Cacheable(value = "staffByEmail", key = "#email")
    public Staff getStaffByEmail(String email) {
        logger.debug("Fetching staff by email: {}", email);
        return staffRepository.findByEmail(email).orElse(null);
    }

    @Cacheable(value = "staffByUsername", key = "#username")
    public Staff getStaffByUsername(String username) {
        logger.debug("Fetching staff by username: {}", username);
        return staffRepository.findByUsername(username).orElse(null);
    }

    @Cacheable(value = "staffById", key = "#id")
    public Staff getStaffById(String id) {
        logger.debug("Fetching staff by ID: {}", id);
        return staffRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "staffByPhoneNumber", key = "#phonenumber")
    public Staff getStaffByPhoneNumber(String phonenumber) {
        logger.debug("Fetching staff by phone number: {}", phonenumber);
        return staffRepository.findByPhoneNumber(phonenumber).orElse(null);
    }

    public byte[] getStaffPhoto(String photoId) throws IOException {
        if (photoId == null || photoId.isEmpty()) {
            logger.warn("Photo ID is null or empty");
            return null;
        }
        try {
            ObjectId objectId = new ObjectId(photoId);
            Query query = new Query(where("_id").is(objectId));
            GridFSFile file = gridFsTemplate.findOne(query);
            GridFsResource resource = gridFsTemplate.getResource(file);
            if (resource == null || !resource.exists()) {
                logger.warn("No GridFS resource found for photo ID: {}", photoId);
                return null;
            }
            try (InputStream inputStream = resource.getInputStream();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                logger.info("Successfully retrieved photo for ID: {}", photoId);
                return outputStream.toByteArray();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid ObjectId format for photo ID: {}", photoId, e);
            return null;
        } catch (IOException e) {
            logger.error("IO error retrieving photo for ID: {}", photoId, e);
            throw e;
        }
    }

    public Map<String, Object> getStaffPhotoWithContentType(String photoId) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("content", getStaffPhoto(photoId));
        result.put("contentType", getContentType(photoId));
        return result;
    }

    public Map<String, Map<String, Object>> getStaffPhotos(String photoId, String documentPhotoId) throws IOException {
        Map<String, Map<String, Object>> photos = new HashMap<>();
        if (photoId != null && !photoId.isEmpty()) {
            photos.put("photo", getStaffPhotoWithContentType(photoId));
        }
        if (documentPhotoId != null && !documentPhotoId.isEmpty()) {
            photos.put("documentphoto", getStaffPhotoWithContentType(documentPhotoId));
        }
        return photos;
    }

    public String getContentType(String photoId) {
        if (photoId == null || photoId.isEmpty()) {
            logger.warn("Photo ID is null or empty");
            return null;
        }
        try {
            ObjectId objectId = new ObjectId(photoId);
            Query query = new Query(where("_id").is(objectId));
            GridFSFile file = gridFsTemplate.findOne(query);
            if (file.getMetadata() == null) {
                logger.warn("No metadata found for photo ID: {}", photoId);
                return FALLBACK_CONTENT_TYPE;
            }
            // Check for standard metadata.contentType
            String contentType = file.getMetadata().getString("contentType");
            if (contentType == null) {
                // Check for nested metadata.metadata.contentType (legacy files)
                Object nestedMetadata = file.getMetadata().get("metadata");
                if (nestedMetadata instanceof org.bson.Document) {
                    contentType = ((org.bson.Document) nestedMetadata).getString("contentType");
                }
                if (contentType == null) {
                    logger.warn("No content type found in metadata for photo ID: {}", photoId);
                    return FALLBACK_CONTENT_TYPE;
                }
            }
            return contentType;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid ObjectId format for photo ID: {}", photoId, e);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving content type for photo ID: {}", photoId, e);
            return FALLBACK_CONTENT_TYPE;
        }
    }

    private void validateFile(MultipartFile file, String fieldName) {
        if (file == null || file.isEmpty()) {
            return;
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            logger.warn("Validation failed: {} size exceeds limit of 5MB", fieldName);
            throw new IllegalArgumentException(fieldName + " size exceeds limit of 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType)) {
            logger.warn("Validation failed: Invalid {} content type: {}", fieldName, contentType);
            throw new IllegalArgumentException("Invalid " + fieldName + " content type: " + contentType);
        }
    }

    private void validateRole(String role) {
        if (role == null
                || !Arrays.asList("ADMIN", "TAILOR", "CUTTING_MASTER", "ASSISTANT", "MANAGER").contains(role)) {
            logger.warn("Validation failed: Invalid role: {}", role);
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
    public List<StaffListDTO> getAllStaff() {
        logger.debug("Fetching all staff members");
        List<Staff> staffList = staffRepository.findAll();
        return staffList.stream().map(staff -> {
            return new StaffListDTO(
                    staff.getId(),
                    staff.getName()
            );
        }).collect(Collectors.toList());
    }
}