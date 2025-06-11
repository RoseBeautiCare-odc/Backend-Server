package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StaffRepository extends MongoRepository<Staff, String> {
    Staff findByEmail(String email);
    
}
