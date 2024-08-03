package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rocketFoodDelivery.rocketFood.models.Address;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// Annotation to indicate that this interface is a Spring Data repository
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // Method to find an Employee by its ID
    Optional<Employee> findById(int id);

    // Method to find an Employee by its associated UserEntity ID
    Optional<Employee> findByUserEntityId(int id);

    // Override of the default deleteById method from JpaRepository
    @Override
    void deleteById(Integer employeeId);

    // Custom query to find Employees associated with a specific Restaurant ID
    @Query(nativeQuery = true, value = "SELECT e.* FROM employees e " +
            "JOIN users u ON e.user_id = u.id " +
            "JOIN restaurants r ON u.id = r.user_id " +
            "WHERE r.id = :restaurantId")
    List<Employee> findEmployeesByRestaurantId(@Param("restaurantId") int restaurantId);

    // Method to find Employees by Address
    List<Employee> findByAddress(Address address);
}
