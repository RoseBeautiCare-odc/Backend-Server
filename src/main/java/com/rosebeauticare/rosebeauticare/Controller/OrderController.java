package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.Model.Order;
import com.rosebeauticare.rosebeauticare.Service.OrderService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping(consumes = { "multipart/form-data" })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> createOrder(
            @RequestPart("order") Order order,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            Order savedOrder = orderService.createOrder(order, images);
            logger.info("Order created successfully: {}", savedOrder.getId());
            return ResponseEntity.ok(new OrderResponseDTO(savedOrder.getId(), "Order created successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input while creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        try {
            if (!ObjectId.isValid(id)) {
                logger.error("Invalid ObjectId format: {}", id);
                return ResponseEntity.badRequest().body("Invalid order ID format");
            }
            Order order = orderService.getOrderById(id);
            logger.info("Fetched order: {}", id);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for order ID: {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching order ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getOrdersByCustomerId(@PathVariable String customerId) {
        try {
            if (!ObjectId.isValid(customerId)) {
                logger.error("Invalid ObjectId format: {}", customerId);
                return ResponseEntity.badRequest().body("Invalid customer ID format");
            }
            List<Order> orders = orderService.getOrdersByCustomerId(customerId);
            logger.info("Fetched {} orders for customer ID: {}", orders.size(), customerId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for customer ID: {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching orders for customer ID: {}: {}", customerId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("/customer/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            logger.info("Fetched {} orders", orders.size());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching all orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }
}

class OrderResponseDTO {
    private String id;
    private String message;

    public OrderResponseDTO(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}