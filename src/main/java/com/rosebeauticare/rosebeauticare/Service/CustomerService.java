package com.rosebeauticare.rosebeauticare.Service;

import com.rosebeauticare.rosebeauticare.Model.Customer;
import com.rosebeauticare.rosebeauticare.Repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    public Customer addCustomer(Customer customer) {
        logger.info("Adding customer with phonenumber: {}", customer.getPhonenumber());
        customer.setCreatedAt(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(String id, Customer updatedCustomer) {
        logger.info("Updating customer with ID: {}", id);
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (!existingCustomer.isPresent()) {
            logger.warn("Customer not found for ID: {}", id);
            throw new IllegalArgumentException("Customer not found");
        }

        Customer customer = existingCustomer.get();
        if (updatedCustomer.getName() != null) {
            customer.setName(updatedCustomer.getName());
        }
        if (updatedCustomer.getPhonenumber() != null) {
            customer.setPhonenumber(updatedCustomer.getPhonenumber());
        }
        if (updatedCustomer.getAlternatePhoneNumber() != null) {
            customer.setAlternatePhoneNumber(updatedCustomer.getAlternatePhoneNumber());
        }
        if (updatedCustomer.getMaritalstatus() != null) {
            customer.setMaritalstatus(updatedCustomer.getMaritalstatus());
        }
        if (updatedCustomer.getAddress() != null) {
            customer.setAddress(updatedCustomer.getAddress());
        }
        if (updatedCustomer.getGender() != null) {
            customer.setGender(updatedCustomer.getGender());
        }
        if (updatedCustomer.getCustomerSince() != null) {
            customer.setCustomerSince(updatedCustomer.getCustomerSince());
        }

        return customerRepository.save(customer);
    }

    public void deleteCustomer(String id) {
        logger.info("Deleting customer with ID: {}", id);
        if (!customerRepository.existsById(id)) {
            logger.warn("Customer not found for ID: {}", id);
            throw new IllegalArgumentException("Customer not found");
        }
        customerRepository.deleteById(id);
    }

    public List<Customer> searchCustomers(String query) {
        logger.info("Searching customers with query: {}", query);
        return customerRepository.searchByQuery(query);
    }

    public List<Customer> searchCustomersRealTime(String query, String type) {
        logger.info("Real-time search with query: {}, type: {}", query, type);
        if (!type.equals("name") && !type.equals("phonenumber")) {
            logger.warn("Invalid search type: {}", type);
            throw new IllegalArgumentException("Invalid search type. Use 'name' or 'phonenumber'.");
        }
        return customerRepository.findByField(type, query);
    }

    public List<Customer> getAllCustomers() {
        logger.info("Fetching all customers");
        return customerRepository.findAll();
    }
    
    public Optional<Customer> getCustomerByPhonenumber(String phonenumber) {
        return customerRepository.findByPhonenumber(phonenumber);
    }

    public Optional<Customer> getCustomerByAlternatePhoneNumber(String alternatePhoneNumber) {
        return customerRepository.findByAlternatePhoneNumber(alternatePhoneNumber);
    }

    public Optional<Customer> getCustomerByName(String name) {
        return customerRepository.findByName(name);
    }

    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }
}