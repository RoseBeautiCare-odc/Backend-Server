package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.Model.Customer;
import com.rosebeauticare.rosebeauticare.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createCustomer(@RequestBody Customer customer) {
        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully created customer: " + createdCustomer.getId());
            response.put("data", createdCustomer);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable String id) {
        try {
            Customer customer = customerService.getCustomerById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved customer: " + id);
            response.put("data", customer);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/phonenumber/{phonenumber}")
    public ResponseEntity<Map<String, Object>> getCustomerByPhonenumber(@PathVariable String phonenumber) {
        try {
            Customer customer = customerService.getCustomerByPhonenumber(phonenumber);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved customer: " + customer.getId());
            response.put("data", customer);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> getCustomerByEmail(@PathVariable String email) {
        try {
            Customer customer = customerService.getCustomerByEmail(email);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully retrieved customer: " + customer.getId());
            response.put("data", customer);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully retrieved all customers");
        response.put("data", customers);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully updated the customer: " + id);
            response.put("data", updatedCustomer);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable String id) {
        try {
            customerService.deleteCustomer(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully deleted customer: " + id);
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}