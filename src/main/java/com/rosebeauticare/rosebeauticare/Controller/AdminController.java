package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private StaffService staffService;

    @PostMapping(value = "/staff", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Staff> addStaff(
            @RequestPart("name") String name,
            @RequestPart("email") String email,
            @RequestPart("dateofbirth") String dateofbirth,
            @RequestPart("phonenumber") String phonenumber,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart("role") String role,
            @RequestPart("securitypin") String securitypin,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            Staff staff = new Staff();
            staff.setName(name);
            staff.setEmail(email);
            staff.setDateofbirth(dateofbirth);
            staff.setPhonenumber(phonenumber);
            staff.setAddress(address);
            staff.setRole(role);
            staff.setSecuritypin(securitypin);
            Staff savedStaff = staffService.addStaff(staff, photo);
            return ResponseEntity.ok(savedStaff);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/setup-admin", consumes = {"multipart/form-data"})
    public ResponseEntity<Staff> setupAdmin(
            @RequestPart("name") String name,
            @RequestPart("email") String email,
            @RequestPart("dateofbirth") String dateofbirth,
            @RequestPart("phonenumber") String phonenumber,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart("securitypin") String securitypin,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            Staff staff = new Staff();
            staff.setName(name);
            staff.setEmail(email);
            staff.setDateofbirth(dateofbirth);
            staff.setPhonenumber(phonenumber);
            staff.setAddress(address);
            staff.setRole("ADMIN");
            staff.setSecuritypin(securitypin);
            Staff savedStaff = staffService.addStaff(staff, photo);
            return ResponseEntity.ok(savedStaff);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}