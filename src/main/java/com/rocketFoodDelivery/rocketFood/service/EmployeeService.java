package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.Employee;
import com.rocketFoodDelivery.rocketFood.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // This annotation indicates that the class is a service component in Spring
public class EmployeeService {

    // Dependency injection of EmployeeRepository
    private final EmployeeRepository employeeRepository;

    // Constructor for dependency injection
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Retrieves all employees.
     *
     * @return A list of all Employee objects.
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Finds an employee by their ID.
     *
     * @param id The ID of the employee.
     * @return An Optional containing the Employee if found, or empty if not.
     */
    public Optional<Employee> findById(int id) {
        return employeeRepository.findById(id);
    }
}
