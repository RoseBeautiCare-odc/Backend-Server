package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    // Find orders by customer ID
    List<Order> findByCustomerId(String customerId);
    
    // Find orders by staff ID (who created the order)
    List<Order> findByStaffId(String staffId);
    
    // Find orders by status
    List<Order> findByStatus(String status);
    
    // Find orders where a specific staff is assigned as cutting master or tailor in any item
    @Query("{ 'items': { $elemMatch: { $or: [ { 'cuttingMaster': ?0 }, { 'tailor': ?0 } ] } } }")
    List<Order> findByAssignedStaff(String staffId);
}