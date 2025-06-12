package com.rosebeauticare.rosebeauticare.Service;

import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GridFsService gridFsService;

    public Staff addStaff(Staff staff, MultipartFile photo, MultipartFile documentphoto) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            String fileId = gridFsService.storeFile(photo);
            staff.setPhoto(fileId);
        } else {
            staff.setPhoto("");
        }
        if(documentphoto != null && !documentphoto.isEmpty()) {
            String documentFileId = gridFsService.storeFile(documentphoto);
            staff.setDocumentphoto(documentFileId);
        } else {
            staff.setDocumentphoto("");
        }
        staff.setSecuritypin(passwordEncoder.encode(staff.getSecuritypin()));
        return staffRepository.save(staff);
    }

    public Staff updateStaff(String id, Staff updatedStaff, MultipartFile photo, MultipartFile documentphoto) throws IOException {
        Staff existingStaff = staffRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Staff not found with id: " + id));

        // Update only provided fields
        if (updatedStaff.getName() != null) existingStaff.setName(updatedStaff.getName());
        if (updatedStaff.getPhonenumber() != null) existingStaff.setPhonenumber(updatedStaff.getPhonenumber());
        if (updatedStaff.getAlternatephonenumber() != null) existingStaff.setAlternatephonenumber(updatedStaff.getAlternatephonenumber());
        if (updatedStaff.getEmail() != null) existingStaff.setEmail(updatedStaff.getEmail());
        if (updatedStaff.getDateofbirth() != null) existingStaff.setDateofbirth(updatedStaff.getDateofbirth());
        if (updatedStaff.getAge() != null) existingStaff.setAge(updatedStaff.getAge());
        if (updatedStaff.getSex() != null) existingStaff.setSex(updatedStaff.getSex());
        if (updatedStaff.getMaritalstatus() != null) existingStaff.setMaritalstatus(updatedStaff.getMaritalstatus());
        if (updatedStaff.getJoineddate() != null) existingStaff.setJoineddate(updatedStaff.getJoineddate());
        if (updatedStaff.getAddress() != null) existingStaff.setAddress(updatedStaff.getAddress());
        if (updatedStaff.getRole() != null) existingStaff.setRole(updatedStaff.getRole());
        if (updatedStaff.getSecuritypin() != null) existingStaff.setSecuritypin(passwordEncoder.encode(updatedStaff.getSecuritypin()));
        

        // Handle file uploads
        if (photo != null && !photo.isEmpty()) {
            String fileId = gridFsService.storeFile(photo);
            existingStaff.setPhoto(fileId);
        }
        if (documentphoto != null && !documentphoto.isEmpty()) {
            String documentFileId = gridFsService.storeFile(documentphoto);
            existingStaff.setDocumentphoto(documentFileId);
        }

        return staffRepository.save(existingStaff);
    }

    public Staff getStaffByEmail(String email) {
        return staffRepository.findByEmail(email).orElse(null);
    }

    public Staff getStaffById(String id) {
        return staffRepository.findById(id).orElse(null);
    }
}