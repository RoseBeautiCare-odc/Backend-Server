package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByCustomerId(String customerId);
}