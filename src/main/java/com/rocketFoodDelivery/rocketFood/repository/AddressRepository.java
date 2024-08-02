package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Annotation to indicate that this interface is a Spring Data repository
@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    // Method to find an Address by its ID
    Optional<Address> findById(int id);

    // Method to find all Address entities ordered by their ID in descending order
    List<Address> findAllByOrderByIdDesc();

    // Annotation to indicate that the method will modify the database
    @Modifying
    // Annotation to indicate that the method should be executed within a
    // transaction
    @Transactional
    // Custom query to insert a new Address into the addresses table
    @Query(nativeQuery = true, value = "INSERT INTO addresses (street_address, city, postal_code) VALUES (:streetAddress, :city, :postalCode)")
    void saveAddress(String streetAddress, String city, String postalCode);

    // Custom query to get the last inserted ID from the addresses table
    @Query(nativeQuery = true, value = "SELECT LAST_INSERT_ID() AS id")
    int getLastInsertedId();
}
