package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.ProductOrder;
import com.rocketFoodDelivery.rocketFood.repository.ProductOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductOrderService {

    private final ProductOrderRepository productOrderRepository;

    // Constructor for dependency injection of ProductOrderRepository
    @Autowired
    public ProductOrderService(ProductOrderRepository productOrderRepository) {
        this.productOrderRepository = productOrderRepository;
    }

    /**
     * Retrieves all product orders.
     *
     * @return a list of all product orders.
     */
    public List<ProductOrder> getAllProductOrders() {
        return productOrderRepository.findAll();
    }

    /**
     * Finds a product order by its ID.
     *
     * @param id the ID of the product order.
     * @return an optional containing the product order if found, or empty if not
     *         found.
     */
    public Optional<ProductOrder> getProductOrderById(int id) {
        return productOrderRepository.findById(id);
    }

    /**
     * Retrieves all product orders for a specific order ID.
     *
     * @param orderId the ID of the order.
     * @return a list of product orders for the specified order ID.
     */
    public List<ProductOrder> getProductOrdersByOrderId(int orderId) {
        return productOrderRepository.findByOrderId(orderId);
    }

    /**
     * Retrieves all product orders for a specific product ID.
     *
     * @param productId the ID of the product.
     * @return a list of product orders for the specified product ID.
     */
    public List<ProductOrder> getProductOrdersByProductId(int productId) {
        return productOrderRepository.findByProductId(productId);
    }

    /**
     * Saves a product order.
     *
     * @param productOrder the product order to save.
     * @return the saved product order.
     */
    public ProductOrder saveProductOrder(ProductOrder productOrder) {
        return productOrderRepository.save(productOrder);
    }

    /**
     * Deletes a product order by its ID.
     *
     * @param productOrderId the ID of the product order to delete.
     */
    public void deleteProductOrderById(int productOrderId) {
        productOrderRepository.deleteById(productOrderId);
    }

    /**
     * Deletes all product orders for a specific order ID.
     *
     * @param orderId the ID of the order.
     */
    public void deleteProductOrdersByOrderId(int orderId) {
        productOrderRepository.deleteProductOrdersByOrderId(orderId);
    }
}
