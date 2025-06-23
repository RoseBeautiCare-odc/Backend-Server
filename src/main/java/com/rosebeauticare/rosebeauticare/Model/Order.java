package com.rosebeauticare.rosebeauticare.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Document(collection = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
    @Id
    private String id;
    private String customerId;
    private String customerName;
    private LocalDate orderDate;
    private LocalDate dueDate;
    private String staffId;
    private String staffName;
    private String MeasurementId;
    private List<ClothItem> clothItems;
    private List<Image> images;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothItem {
        private String serviceType; 
        private String subService; 
        private Customization customization;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customization {
        private String type; 
        private Map<String, Object> details; 
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        private String fileId; 
        private String contentType;
    }
}