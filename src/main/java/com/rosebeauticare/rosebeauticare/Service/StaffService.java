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

    public Staff addStaff(Staff staff, MultipartFile photo) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            String fileId = gridFsService.storeFile(photo);
            staff.setPhoto(fileId);
        } else {
            staff.setPhoto("");
        }
        staff.setSecuritypin(passwordEncoder.encode(staff.getSecuritypin()));
        return staffRepository.save(staff);
    }
}