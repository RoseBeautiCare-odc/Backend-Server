package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface StaffRepository extends MongoRepository<Staff, String> {
    Optional<Staff> findByEmail(String email);
     @NonNull Optional<Staff> findById(@NonNull String id);
}