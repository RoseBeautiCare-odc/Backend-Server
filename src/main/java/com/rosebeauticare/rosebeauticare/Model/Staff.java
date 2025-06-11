package com.rosebeauticare.rosebeauticare.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Document(collection ="staff")
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Staff {
    @Id
    private String id;
    private String name;
    private String email;
    private String dateofbirth;
    private String phonenumber;
    private String address;
    private String photo;
    private String role;
    private String securitypin;

    
}
