package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.models.Address;

import java.util.Optional;
import java.util.List;

// Annotation to indicate that this interface is a Spring Data repository
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Method to find a Customer by its ID
    Optional<Customer> findById(int id);

    // Method to find a Customer by its associated UserEntity ID
    Optional<Customer> findByUserEntityId(int id);

    // Method to find a Customer by its associated UserEntity and Address
    Optional<Customer> findByUserEntityAndAddress(UserEntity userEntity, Address address);

    // Method to find a Customer by its associated UserEntity
    Optional<Customer> findByUserEntity(UserEntity userEntity);

    // Method to find Customers by Address
    List<Customer> findByAddress(Address address);
}
