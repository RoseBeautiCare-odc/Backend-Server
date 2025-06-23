package com.rosebeauticare.rosebeauticare.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "staff")
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Staff {
    @Id
    private String id;
    private String name;
    private String username;
    private String phonenumber;
    private String alternatephonenumber;
    private String email;
    private String dateofbirth;
    private String age;
    private String sex;
    private String maritalstatus;
    private String joineddate;
    private String address;
    private String photo;
    private String documentType;
    private String documentphoto;
    private String role;
    private String securitypin;
}
