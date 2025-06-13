package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.Model.Order;
import com.rosebeauticare.rosebeauticare.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Create a new order (admin or manager only)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestPart("order") Order order,
            @RequestPart(value = "clothImages", required = false) MultipartFile[] clothImages) throws IOException {
        try {
            Order createdOrder = orderService.createOrder(order, clothImages);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully created order: " + createdOrder.getOrderNumber());
            response.put("data", createdOrder);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get order by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TAILOR', 'CUTTING_MASTER')")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable String id) {
        try {
            Order order = orderService.getOrderById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved order: " + order.getOrderNumber());
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }

    // Get orders by customer ID
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getOrdersByCustomer(@PathVariable String customerId) {
        try {
            List<Order> orders = orderService.getOrdersByCustomerId(customerId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved orders for customer: " + customerId);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }

    // Get orders by staff ID (created or assigned)
    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TAILOR', 'CUTTING_MASTER')")
    public ResponseEntity<Map<String, Object>> getOrdersByStaff(@PathVariable String staffId) {
        try {
            List<Order> orders = orderService.getOrdersByStaffId(staffId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved orders for staff: " + staffId);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }

    // Update item status (e.g., cutting completed)
    @PutMapping("/{id}/items/{itemId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TAILOR', 'CUTTING_MASTER')")
    public ResponseEntity<Map<String, Object>> updateItemStatus(
            @PathVariable String id,
            @PathVariable String itemId,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String status = statusUpdate.get("status");
            Order updatedOrder = orderService.updateItemStatus(id, itemId, status);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully updated item status for order: " + updatedOrder.getOrderNumber());
            response.put("data", updatedOrder);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Assign cutting master or tailor to an item (admin only)
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> assignStaff(
            @PathVariable String id,
            @RequestBody Map<String, Object> assignment) {
        try {
            String itemId = (String) assignment.get("itemId");
            String cuttingMaster = (String) assignment.get("cuttingMaster");
            String tailor = (String) assignment.get("tailor");
            Order updatedOrder = orderService.assignStaff(id, itemId, cuttingMaster, tailor);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully assigned staff for order: " + updatedOrder.getOrderNumber());
            response.put("data", updatedOrder);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get all orders (admin or manager only)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully retrieved all orders");
        response.put("data", orders);
        return ResponseEntity.ok(response);
    }
    // Delete an order (admin or manager only)
    @DeleteMapping("/{id}") 
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable String id) {
        try {
            orderService.deleteOrder(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully deleted order with ID: " + id);
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }
    // Update an order (admin or manager only)
    @PutMapping("/{id}")    
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> updateOrder(
            @PathVariable String id,
            @RequestPart("order") Order updatedOrder,
            @RequestPart(value = "clothImages", required = false) MultipartFile[] clothImages) throws IOException {
        try {
            Order order = orderService.updateOrder(id, updatedOrder, clothImages);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully updated order: " + order.getOrderNumber());
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }
    // Get orders by status (admin or manager only)
    @GetMapping("/status/{status}")     
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getOrdersByStatus(@PathVariable String status) {
        try {
            List<Order> orders = orderService.getOrdersByStatus(status);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved orders with status: " + status);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }
    // Get orders assigned to a specific staff (admin or manager only)
    @GetMapping("/assigned/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getOrdersAssignedToStaff(@PathVariable String staffId) {
        try {
            List<Order> orders = orderService.getOrdersAssignedToStaff(staffId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved orders assigned to staff: " + staffId);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }
    
}
