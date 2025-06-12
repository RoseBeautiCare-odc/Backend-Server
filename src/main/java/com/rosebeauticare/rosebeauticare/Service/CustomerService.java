package com.rosebeauticare.rosebeauticare.Service;

import com.rosebeauticare.rosebeauticare.Model.Customer;
import com.rosebeauticare.rosebeauticare.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (customer.getPhonenumber() == null || customer.getPhonenumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (customer.getAddress() == null || 
            customer.getAddress().getTownOrVillage() == null || customer.getAddress().getTownOrVillage().isEmpty() ||
            customer.getAddress().getDistrict() == null || customer.getAddress().getDistrict().isEmpty() ||
            customer.getAddress().getState() == null || customer.getAddress().getState().isEmpty()) {
            throw new IllegalArgumentException("Complete address is required");
        }
        customer.setCreatedAt(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));
    }

    public Customer getCustomerByPhonenumber(String phonenumber) {
        Customer customer = customerRepository.findByPhonenumber(phonenumber);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with phonenumber: " + phonenumber);
        }
        return customer;
    }

    public Customer getCustomerByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required for search");
        }
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with email: " + email);
        }
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(String id, Customer updatedCustomer) {
        Customer existingCustomer = getCustomerById(id);
        if (updatedCustomer.getName() != null) existingCustomer.setName(updatedCustomer.getName());
        if (updatedCustomer.getPhonenumber() != null) existingCustomer.setPhonenumber(updatedCustomer.getPhonenumber());
        if (updatedCustomer.getAddress() != null) {
            if (updatedCustomer.getAddress().getTownOrVillage() != null) {
                existingCustomer.getAddress().setTownOrVillage(updatedCustomer.getAddress().getTownOrVillage());
            }
            if (updatedCustomer.getAddress().getDistrict() != null) {
                existingCustomer.getAddress().setDistrict(updatedCustomer.getAddress().getDistrict());
            }
            if (updatedCustomer.getAddress().getState() != null) {
                existingCustomer.getAddress().setState(updatedCustomer.getAddress().getState());
            }
        }
        if (updatedCustomer.getEmail() != null) existingCustomer.setEmail(updatedCustomer.getEmail());
        if (updatedCustomer.getSex() != null) existingCustomer.setSex(updatedCustomer.getSex());
        if (updatedCustomer.getMaritalstatus() != null) existingCustomer.setMaritalstatus(updatedCustomer.getMaritalstatus());
        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(String id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}