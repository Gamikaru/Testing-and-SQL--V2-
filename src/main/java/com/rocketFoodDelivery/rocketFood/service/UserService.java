package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.Employee;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.EmployeeRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Constructor for dependency injection of UserRepository and EmployeeRepository.
     * 
     * @param userRepository the repository for users.
     * @param employeeRepository the repository for employees.
     */
    @Autowired
    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Populates the employee status in a UserEntity.
     * 
     * @param userEntity the user entity to update.
     */
    public void populateEmployeeStatus(UserEntity userEntity) {
        Optional<Employee> employee = employeeRepository.findByUserEntityId(userEntity.getId());
        userEntity.setEmployee(employee.isPresent());
    }

    /**
     * Retrieves all users.
     * 
     * @return a list of all users.
     */
    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds a user by ID.
     * 
     * @param id the ID of the user.
     * @return an optional containing the user if found.
     */
    public Optional<UserEntity> findById(int id) {
        return userRepository.findById(id);
    }

    /**
     * Finds the last inserted user ID by ordering in descending order.
     * 
     * @return an optional containing the last user ID if found.
     */
    public Optional<Integer> findLastUserId() {
        List<UserEntity> users = userRepository.findAllByOrderByIdDesc();
        if (!users.isEmpty()) {
            return Optional.of(users.get(0).getId());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Saves a UserEntity.
     * 
     * @param user the user entity to save.
     * @return the saved user entity.
     */
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    /**
     * Deletes a user by ID.
     * 
     * @param id the ID of the user to delete.
     */
    public void delete(int id) {
        userRepository.deleteById(id);
    }
}
