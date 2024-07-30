package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findById(int id);

    Optional<Employee> findByUserEntityId(int id);

    @Override
    void deleteById(Integer employeeId);

    @Query(nativeQuery = true, value = "SELECT e.* FROM employees e " +
            "JOIN users u ON e.user_id = u.id " +
            "JOIN restaurants r ON u.id = r.user_id " +
            "WHERE r.id = :restaurantId")
    List<Employee> findEmployeesByRestaurantId(@Param("restaurantId") int restaurantId);
}
