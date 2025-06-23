package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByPhonenumber(String phonenumber);
    Optional<Customer> findByAlternatePhoneNumber(String alternatePhoneNumber);
    Optional<Customer> findByName(String name);

    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'phonenumber': { $regex: ?0, $options: 'i' } }, { 'alternatePhoneNumber': { $regex: ?0, $options: 'i' } } ] }")
    List<Customer> searchByQuery(String query);

    @Query("{ ?0: { $regex: ?1, $options: 'i' } }")
    List<Customer> findByField(String field, String query);
}