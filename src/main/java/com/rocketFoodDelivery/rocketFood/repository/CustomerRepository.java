package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.models.Address;


import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findById(int id);

    Optional<Customer> findByUserEntityId(int id);

    Optional<Customer> findByUserEntityAndAddress(UserEntity userEntity, Address address);

    Optional<Customer> findByUserEntity(UserEntity userEntity);

}
