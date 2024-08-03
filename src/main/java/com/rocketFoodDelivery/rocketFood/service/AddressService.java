package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // This annotation indicates that the class is a service component in Spring
public class AddressService {
    private final AddressRepository addressRepository;

    // Constructor for dependency injection of the AddressRepository
    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Find an address by its ID.
     *
     * @param id The ID of the address.
     * @return An Optional containing the Address if found, or empty if not.
     */
    public Optional<Address> findById(int id) {
        return addressRepository.findById(id);
    }

    /**
     * Find the last inserted address ID by ordering in descending order.
     *
     * @return An Optional containing the last address ID if found, or empty if not.
     */
    public Optional<Integer> findLastAddressId() {
        List<Address> addresses = addressRepository.findAllByOrderByIdDesc();
        if (!addresses.isEmpty()) {
            return Optional.of(addresses.get(0).getId());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Save an Address object using the repository.
     *
     * @param address The Address object to be saved.
     * @return The saved Address object.
     */
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    /**
     * Transactional method to save an address using custom query and retrieve the
     * last inserted ID.
     *
     * @param streetAddress The street address.
     * @param city          The city.
     * @param postalCode    The postal code.
     * @return The ID of the last inserted address, or -1 if there was an exception.
     */
    @Transactional
    public int saveAddress(String streetAddress, String city, String postalCode) {
        try {
            addressRepository.saveAddress(streetAddress, city, postalCode);
            return addressRepository.getLastInsertedId();
        } catch (DataAccessException e) {
            e.printStackTrace(); // Log the exception stack trace
            return -1; // Return -1 if there was an exception
        }
    }

    /**
     * Delete an address by its ID.
     *
     * @param id The ID of the address to be deleted.
     */
    public void delete(int id) {
        addressRepository.deleteById(id);
    }
}
