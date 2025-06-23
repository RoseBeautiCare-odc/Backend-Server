package com.rosebeauticare.rosebeauticare.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "measurements")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Measurement {
    @Id
    private String id;
    private String customerId;
    private String serviceType;
    private Map<String, Double> measurementDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
