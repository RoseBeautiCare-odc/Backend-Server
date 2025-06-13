package com.rosebeauticare.rosebeauticare.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@NoArgsConstructor  
@AllArgsConstructor
@Data
public class Order {
    @Id
    private String id;
    private String orderNumber;
    private String customerId;
    private String staffId;
    private String serviceType;
    private String subType;
    private String measurementId;
    private List<Item> items;
    private String status;
    //completed
    private Double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private  String itemId;
        private String  status;
        private String cuttingMaster;
        private String tailor;
        private LocalDateTime cuttingCompletedAt;
        private LocalDateTime stitchingCompletedAt;
        private LocalDateTime additionalworkCompletedAt;
        private LocalDateTime ironingCompletedAt;
        private LocalDateTime qualityCheckCompletedAt;
        private Double price;
        private String clothImage;
        private String notes;
    }
       
}