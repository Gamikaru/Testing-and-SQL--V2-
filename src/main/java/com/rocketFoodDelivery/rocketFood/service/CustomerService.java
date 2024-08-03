package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.Customer;
import com.rocketFoodDelivery.rocketFood.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // This annotation indicates that the class is a service component in Spring
public class CustomerService {

    // Dependency injection of CustomerRepository
    private final CustomerRepository customerRepository;

    // Constructor for dependency injection
    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Retrieves all customers.
     *
     * @return A list of all Customer objects.
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Finds a customer by their ID.
     *
     * @param id The ID of the customer.
     * @return An Optional containing the Customer if found, or empty if not.
     */
    public Optional<Customer> findById(int id) {
        return customerRepository.findById(id);
    }
}
