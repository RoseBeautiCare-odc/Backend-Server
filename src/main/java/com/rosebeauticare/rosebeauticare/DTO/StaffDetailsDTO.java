package com.rosebeauticare.rosebeauticare.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class StaffDetailsDTO {
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\+91\\d{10}", message = "Phone number must start with +91 and be 10 digits")
    private String phonenumber;

    @Pattern(regexp = "\\+91\\d{10}|", message = "Alternate phone number must start with +91 and be 10 digits or empty")
    private String alternatephonenumber;

    @NotBlank(message = "Email is required")
    private String email;

    private String dateofbirth;
    private String age;

    @NotBlank(message = "Sex is required")
    private String sex;

    @NotBlank(message = "Marital status is required")
    private String maritalstatus;

    @NotBlank(message = "Joined date is required")
    private String joineddate;

    private String address;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Document type is required")
    private String documentType;

    private byte[] photo;
    private String photoContentType;
    private byte[] documentphoto;
    private String documentPhotoContentType;

    public StaffDetailsDTO() {
    }

    public StaffDetailsDTO(String id, String name, String username, String phonenumber, String alternatephonenumber,
            String email, String dateofbirth, String age, String sex, String maritalstatus, String joineddate,
            String address, String role, String documentType, byte[] photo, String photoContentType,
            byte[] documentphoto, String documentPhotoContentType) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.phonenumber = phonenumber;
        this.alternatephonenumber = alternatephonenumber;
        this.email = email;
        this.dateofbirth = dateofbirth;
        this.age = age;
        this.sex = sex;
        this.maritalstatus = maritalstatus;
        this.joineddate = joineddate;
        this.address = address;
        this.role = role;
        this.documentType = documentType;
        this.photo = photo;
        this.photoContentType = photoContentType;
        this.documentphoto = documentphoto;
        this.documentPhotoContentType = documentPhotoContentType;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAlternatephonenumber() {
        return alternatephonenumber;
    }

    public void setAlternatephonenumber(String alternatephonenumber) {
        this.alternatephonenumber = alternatephonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMaritalstatus() {
        return maritalstatus;
    }

    public void setMaritalstatus(String maritalstatus) {
        this.maritalstatus = maritalstatus;
    }

    public String getJoineddate() {
        return joineddate;
    }

    public void setJoineddate(String joineddate) {
        this.joineddate = joineddate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public byte[] getDocumentphoto() {
        return documentphoto;
    }

    public void setDocumentphoto(byte[] documentphoto) {
        this.documentphoto = documentphoto;
    }

    public String getDocumentPhotoContentType() {
        return documentPhotoContentType;
    }

    public void setDocumentPhotoContentType(String documentPhotoContentType) {
        this.documentPhotoContentType = documentPhotoContentType;
    }
}