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

    @Autowired
    public ProductOrderService(ProductOrderRepository productOrderRepository) {
        this.productOrderRepository = productOrderRepository;
    }

    public List<ProductOrder> getAllProductOrders() {
        return productOrderRepository.findAll();
    }

    public Optional<ProductOrder> getProductOrderById(int id) {
        return productOrderRepository.findById(id);
    }

    public List<ProductOrder> getProductOrdersByOrderId(int orderId) {
        return productOrderRepository.findByOrderId(orderId);
    }

    public List<ProductOrder> getProductOrdersByProductId(int productId) {
        return productOrderRepository.findByProductId(productId);
    }

    public ProductOrder saveProductOrder(ProductOrder productOrder) {
        return productOrderRepository.save(productOrder);
    }

    public void deleteProductOrderById(int productOrderId) {
        productOrderRepository.deleteById(productOrderId);
    }

    public void deleteProductOrdersByOrderId(int orderId) {
        productOrderRepository.deleteProductOrdersByOrderId(orderId);
    }
}
