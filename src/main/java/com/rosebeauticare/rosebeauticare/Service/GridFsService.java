package com.rosebeauticare.rosebeauticare.Service;

import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Service
public class GridFsService {

    private static final Logger logger = LoggerFactory.getLogger(GridFsService.class);
    private static final String[] ALLOWED_CONTENT_TYPES = { "image/jpeg", "image/png" };

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String storeFile(MultipartFile file, String staffId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType)) {
            logger.warn("Invalid content type for file: {}", contentType);
            throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new org.bson.Document()
                        .append("contentType", contentType)
                        .append("staffId", staffId));
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                options); // Remove contentType parameter
        logger.info("Stored file in GridFS with ID: {}, contentType: {}, staffId: {}", fileId, contentType, staffId);
        return fileId.toString();
    }

    // Deprecate or remove unused method
    @Deprecated
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType)) {
            logger.warn("Invalid content type for file: {}", contentType);
            throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new org.bson.Document("contentType", contentType));
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                options);
        logger.info("Stored file in GridFS with ID: {}, contentType: {}", fileId, contentType);
        return fileId.toString();
    }
}