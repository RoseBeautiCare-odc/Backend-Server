package com.rosebeauticare.rosebeauticare.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {
    @Id
    private String id;
    private String name;
    private String phonenumber;
    private Address address;
    private String email; // Optional
    private LocalDateTime createdAt;
    private String sex;
    private String maritalstatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String townOrVillage;
        private String district;
        private String state;
    }
}