package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface StaffRepository extends MongoRepository<Staff, String> {
    @Query("{ 'email' : ?0 }")
    Optional<Staff> findByEmail(String email);

    @Query("{ 'username' : { $regex: ?0, $options: 'i' } }")
    Optional<Staff> findByUsername(String username);

    @Query("{ 'phonenumber' : ?0 }")
    Optional<Staff> findByPhoneNumber(String phonenumber);

    @NonNull
    Optional<Staff> findById(@NonNull String id);
}