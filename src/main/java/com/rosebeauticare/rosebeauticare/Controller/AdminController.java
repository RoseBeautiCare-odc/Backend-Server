package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private StaffService staffService;

    @PostMapping(value = "/staff", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Staff> addStaff(
            @RequestPart("name") String name,
            @RequestPart("phonenumber") String phonenumber,
            @RequestPart("alternatephonenumber") String alternatephonenumber,
            @RequestPart("email") String email,
            @RequestPart("dateofbirth") String dateofbirth,
            @RequestPart("age") String age,
            @RequestPart("sex") String sex,
            @RequestPart("maritalstatus") String maritalstatus,
            @RequestPart("role") String role,
            @RequestPart("securitypin") String securitypin,
            @RequestPart("joineddate") String joineddate,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "documentphoto", required = false) MultipartFile documentphoto) {
        try {
            Staff staff = new Staff();
            staff.setName(name);
            staff.setPhonenumber(phonenumber);
            staff.setAlternatephonenumber(alternatephonenumber);
            staff.setJoineddate(joineddate);
            staff.setEmail(email);
            staff.setDateofbirth(dateofbirth);
            staff.setAge(age);
            staff.setSex(sex);
            staff.setMaritalstatus(maritalstatus);
            staff.setAddress(address);
            staff.setRole(role);
            staff.setSecuritypin(securitypin);

            Staff savedStaff = staffService.addStaff(staff, photo, documentphoto);
            return ResponseEntity.ok(savedStaff);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/admin", consumes = { "multipart/form-data" })
    public ResponseEntity<Staff> setupAdmin(
            @RequestPart("name") String name,
            @RequestPart("phonenumber") String phonenumber,
            @RequestPart("alternatephonenumber") String alternatephonenumber,
            @RequestPart("email") String email,
            @RequestPart("dateofbirth") String dateofbirth,
            @RequestPart("age") String age,
            @RequestPart("sex") String sex,
            @RequestPart("maritalstatus") String maritalstatus,
            @RequestPart("joineddate") String joineddate,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart("securitypin") String securitypin,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "photo", required = false) MultipartFile documentphoto) {
        try {
            Staff staff = new Staff();
            staff.setName(name);
            staff.setPhonenumber(phonenumber);
            staff.setAlternatephonenumber(alternatephonenumber);
            staff.setAge(age);
            staff.setJoineddate(joineddate);
            staff.setSex(sex);
            staff.setMaritalstatus(maritalstatus);
            staff.setEmail(email);
            staff.setDateofbirth(dateofbirth);
            staff.setAddress(address);
            staff.setRole("ADMIN");
            staff.setSecuritypin(securitypin);

            Staff savedStaff = staffService.addStaff(staff, photo, documentphoto);
            return ResponseEntity.ok(savedStaff);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/staff/{id}", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Staff> updateStaff(
            @PathVariable String id,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "phonenumber", required = false) String phonenumber,
            @RequestPart(value = "alternatephonenumber", required = false) String alternatephonenumber,
            @RequestPart(value = "email", required = false) String email,
            @RequestPart(value = "dateofbirth", required = false) String dateofbirth,
            @RequestPart(value = "age", required = false) String age,
            @RequestPart(value = "sex", required = false) String sex,
            @RequestPart(value = "maritalstatus", required = false) String maritalstatus,
            @RequestPart(value = "role", required = false) String role,
            @RequestPart(value = "securitypin", required = false) String securitypin,
            @RequestPart(value = "joineddate", required = false) String joineddate,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "documentphoto", required = false) MultipartFile documentphoto) {
        try {
            Staff updatedStaff = new Staff();
            updatedStaff.setName(name);
            updatedStaff.setPhonenumber(phonenumber);
            updatedStaff.setAlternatephonenumber(alternatephonenumber);
            updatedStaff.setEmail(email);
            updatedStaff.setDateofbirth(dateofbirth);
            updatedStaff.setAge(age);
            updatedStaff.setSex(sex);
            updatedStaff.setMaritalstatus(maritalstatus);
            updatedStaff.setRole(role);
            updatedStaff.setSecuritypin(securitypin);
            updatedStaff.setJoineddate(joineddate);
            updatedStaff.setAddress(address);

            Staff savedStaff = staffService.updateStaff(id, updatedStaff, photo, documentphoto);
            return ResponseEntity.ok(savedStaff);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}