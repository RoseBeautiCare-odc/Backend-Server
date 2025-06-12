package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Customer findByPhonenumber(String phonenumber);
    Customer findByEmail(String email);
}