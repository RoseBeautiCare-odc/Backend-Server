package com.rosebeauticare.rosebeauticare.DTO;

public class StaffPhotoDTO {
    private String id;
    private String username;
    private byte[] photo;
    private String photoContentType;

    public StaffPhotoDTO(String id, String username, byte[] photo, String photoContentType) {
        this.id = id;
        this.username = username;
        this.photo = photo;
        this.photoContentType = photoContentType;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}