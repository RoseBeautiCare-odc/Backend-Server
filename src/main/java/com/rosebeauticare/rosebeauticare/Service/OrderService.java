package com.rosebeauticare.rosebeauticare.Service;

import com.rosebeauticare.rosebeauticare.Model.Order;
import com.rosebeauticare.rosebeauticare.Model.Order.Item;
import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Repository.OrderRepository;
import com.rosebeauticare.rosebeauticare.Repository.CustomerRepository;
import com.rosebeauticare.rosebeauticare.Repository.StaffRepository;
import com.rosebeauticare.rosebeauticare.Repository.MeasurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private static final List<String> VALID_STATUSES = Arrays.asList(
            "Pending", "Cutting", "Stitching", "Additional Work", "Ironing", "Quality Check", "Completed");
    private static final List<String> VALID_SERVICE_TYPES = Arrays.asList("Blouse", "Churidar");
    private static final List<String> VALID_SUB_TYPES = Arrays.asList("Simple Blouse", "Designer Blouse",
            "Simple Churidar", "Designer Churidar");
    private static final List<String> VALID_STAFF_ROLES = Arrays.asList("TAILOR", "CUTTING_MASTER");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private GridFsService gridFsService;

    @Autowired
    private MeasurementRepository measurementRepository;

    public Order createOrder(Order order, MultipartFile[] clothImages) throws IOException {
        // Validate required fields
        if (order.getCustomerId() == null || order.getCustomerId().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (order.getStaffId() == null || order.getStaffId().isEmpty()) {
            throw new IllegalArgumentException("Staff ID is required");
        }
        if (order.getServiceType() == null || !VALID_SERVICE_TYPES.contains(order.getServiceType())) {
            throw new IllegalArgumentException("Invalid service type. Allowed: " + VALID_SERVICE_TYPES);
        }
        if (order.getSubType() == null || !VALID_SUB_TYPES.contains(order.getSubType())) {
            throw new IllegalArgumentException("Invalid sub-type. Allowed: " + VALID_SUB_TYPES);
        }
        if (order.getMeasurementId() == null || order.getMeasurementId().isEmpty()) {
            throw new IllegalArgumentException("Measurement ID is required");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one item is required");
        }

        // Validate references
        if (!customerRepository.existsById(order.getCustomerId())) {
            throw new IllegalArgumentException("Customer not found with ID: " + order.getCustomerId());
        }
        if (!staffRepository.existsById(order.getStaffId())) {
            throw new IllegalArgumentException("Staff not found with ID: " + order.getStaffId());
        }
        if (!measurementRepository.existsById(order.getMeasurementId())) {
            throw new IllegalArgumentException("Measurement not found with ID: " + order.getMeasurementId());
        }

        // Validate items and assign item IDs
        int imageIndex = 0;
        for (Item item : order.getItems()) {
            if (item.getPrice() == null || item.getPrice() < 0) {
                throw new IllegalArgumentException("Invalid price for item");
            }
            item.setItemId(UUID.randomUUID().toString());
            item.setStatus("Pending");
            if (clothImages != null && imageIndex < clothImages.length && clothImages[imageIndex] != null && !clothImages[imageIndex].isEmpty()) {
                String fileId = gridFsService.storeFile(clothImages[imageIndex]);
                item.setClothImage(fileId);
                imageIndex++;
            } else {
                item.setClothImage("");
            }
        }

        // Generate unique order number
        String orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);
        order.setStatus("Pending");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setTotalPrice(order.getItems().stream().mapToDouble(Item::getPrice).sum());

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created with ID: {} and order number: {}", savedOrder.getId(), savedOrder.getOrderNumber());
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        logger.info("Retrieved {} orders", orders.size());
        return orders;
    }

    public void deleteOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        if (order.getStatus().equals("Completed")) {
            throw new IllegalArgumentException("Cannot delete completed order: " + order.getOrderNumber());
        }
        orderRepository.delete(order);
        logger.info("Deleted order with ID: {}", id);
    }

    // Update an order
    public Order updateOrder(String id, Order updatedOrder, MultipartFile[] clothImages) throws IOException {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));

        // Validate updated fields
        if (updatedOrder.getCustomerId() != null && !updatedOrder.getCustomerId().isEmpty()) {
            if (!customerRepository.existsById(updatedOrder.getCustomerId())) {
                throw new IllegalArgumentException("Customer not found with ID: " + updatedOrder.getCustomerId());
            }
            existingOrder.setCustomerId(updatedOrder.getCustomerId());
        }
        if (updatedOrder.getStaffId() != null && !updatedOrder.getStaffId().isEmpty()) {
            if (!staffRepository.existsById(updatedOrder.getStaffId())) {
                throw new IllegalArgumentException("Staff not found with ID: " + updatedOrder.getStaffId());
            }
            existingOrder.setStaffId(updatedOrder.getStaffId());
        }
        if (updatedOrder.getServiceType() != null && VALID_SERVICE_TYPES.contains(updatedOrder.getServiceType())) {
            existingOrder.setServiceType(updatedOrder.getServiceType());
        }
        if (updatedOrder.getSubType() != null && VALID_SUB_TYPES.contains(updatedOrder.getSubType())) {
            existingOrder.setSubType(updatedOrder.getSubType());
        }
        if (updatedOrder.getMeasurementId() != null && !updatedOrder.getMeasurementId().isEmpty()) {
            if (!measurementRepository.existsById(updatedOrder.getMeasurementId())) {
                throw new IllegalArgumentException("Measurement not found with ID: " + updatedOrder.getMeasurementId());
            }
            existingOrder.setMeasurementId(updatedOrder.getMeasurementId());
        }
        if (updatedOrder.getItems() != null && !updatedOrder.getItems().isEmpty()) {
            int imageIndex = 0;
            for (Item item : updatedOrder.getItems()) {
                if (item.getPrice() == null || item.getPrice() < 0) {
                    throw new IllegalArgumentException("Invalid price for item");
                }
                if (item.getItemId() == null) {
                    item.setItemId(UUID.randomUUID().toString());
                }
                if (item.getStatus() == null || !VALID_STATUSES.contains(item.getStatus())) {
                    item.setStatus("Pending");
                }
                if (clothImages != null && imageIndex < clothImages.length && clothImages[imageIndex] != null && !clothImages[imageIndex].isEmpty()) {
                    String fileId = gridFsService.storeFile(clothImages[imageIndex]);
                    item.setClothImage(fileId);
                    imageIndex++;
                }
            }
            existingOrder.setItems(updatedOrder.getItems());
            existingOrder.setTotalPrice(updatedOrder.getItems().stream().mapToDouble(Item::getPrice).sum());
        }

        // Update status and timestamps
        existingOrder.setStatus(calculateOrderStatus(existingOrder.getItems()));
        existingOrder.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(existingOrder);
        logger.info("Updated order with ID: {}", id);
        return savedOrder;
    }

    public List<Order> getOrdersByStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid status. Allowed: " + VALID_STATUSES);
        }
        List<Order> orders = orderRepository.findByStatus(status);
        logger.info("Retrieved {} orders with status: {}", orders.size(), status);
        return orders;
    }

    public List<Order> getOrdersAssignedToStaff(String staffId) {
        if (staffId == null || staffId.isEmpty()) {
            throw new IllegalArgumentException("Staff ID is required");
        }
        if (!staffRepository.existsById(staffId)) {
            throw new IllegalArgumentException("Staff not found with ID: " + staffId);
        }
        List<Order> orders = orderRepository.findByAssignedStaff(staffId);
        logger.info("Retrieved {} orders assigned to staff: {}", orders.size(), staffId);
        return orders;
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByStaffId(String staffId) {
        if (staffId == null || staffId.isEmpty()) {
            throw new IllegalArgumentException("Staff ID is required");
        }
        if (!staffRepository.existsById(staffId)) {
            throw new IllegalArgumentException("Staff not found with ID: " + staffId);
        }
        List<Order> createdOrders = orderRepository.findByStaffId(staffId);
        List<Order> assignedOrders = orderRepository.findByAssignedStaff(staffId);
        createdOrders.addAll(assignedOrders);
        return createdOrders.stream().distinct().toList();
    }

    public Order updateItemStatus(String id, String itemId, String status) {
        if (!VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid status. Allowed: " + VALID_STATUSES);
        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        Item item = order.getItems().stream()
                .filter(i -> i.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        // Update item status and timestamps
        item.setStatus(status);
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case "Cutting":
                item.setCuttingCompletedAt(null); // Reset if moving back to Cutting
                break;
            case "Stitching":
                item.setCuttingCompletedAt(now);
                break;
            case "Additional Work":
                item.setStitchingCompletedAt(now);
                break;
            case "Ironing":
                item.setAdditionalworkCompletedAt(now);
                break;
            case "Quality Check":
                item.setIroningCompletedAt(now);
                break;
            case "Completed":
                item.setQualityCheckCompletedAt(now);
                break;
        }

        // Update overall order status
        order.setStatus(calculateOrderStatus(order.getItems()));
        order.setUpdatedAt(now);
        Order savedOrder = orderRepository.save(order);
        logger.info("Updated status for item {} in order {} to {}", itemId, id, status);
        return savedOrder;
    }

    public Order assignStaff(String id, String itemId, String cuttingMaster, String tailor) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        Item item = order.getItems().stream()
                .filter(i -> i.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        // Validate staff roles
        if (cuttingMaster != null) {
            Staff staff = staffRepository.findById(cuttingMaster)
                    .orElseThrow(
                            () -> new IllegalArgumentException("Cutting master not found with ID: " + cuttingMaster));
            if (!VALID_STAFF_ROLES.contains(staff.getRole())) {
                throw new IllegalArgumentException("Assigned staff must have role: " + VALID_STAFF_ROLES);
            }
            item.setCuttingMaster(cuttingMaster);
        }
        if (tailor != null) {
            Staff staff = staffRepository.findById(tailor)
                    .orElseThrow(() -> new IllegalArgumentException("Tailor not found with ID: " + tailor));
            if (!VALID_STAFF_ROLES.contains(staff.getRole())) {
                throw new IllegalArgumentException("Assigned staff must have role: " + VALID_STAFF_ROLES);
            }
            item.setTailor(tailor);
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        logger.info("Assigned staff to item {} in order {}", itemId, id);
        return savedOrder;
    }

    private String generateOrderNumber() {
        // Example: ORD-2025-001
        String year = Year.now().toString();
        long count = orderRepository.count() + 1;
        return String.format("ORD-%s-%03d", year, count);
    }

    private String calculateOrderStatus(List<Item> items) {
        if (items.stream().allMatch(item -> item.getStatus().equals("Completed"))) {
            return "Completed";
        }
        if (items.stream().anyMatch(item -> item.getStatus().equals("Quality Check"))) {
            return "Quality Check";
        }
        if (items.stream().anyMatch(item -> item.getStatus().equals("Ironing"))) {
            return "Ironing";
        }
        if (items.stream().anyMatch(item -> item.getStatus().equals("Additional Work"))) {
            return "Additional Work";
        }
        if (items.stream().anyMatch(item -> item.getStatus().equals("Stitching"))) {
            return "Stitching";
        }
        if (items.stream().anyMatch(item -> item.getStatus().equals("Cutting"))) {
            return "Cutting";
        }
        return "Pending";
    }
}