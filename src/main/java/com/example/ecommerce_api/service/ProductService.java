package com.example.ecommerce_api.service;

import com.example.ecommerce_api.model.Product;
import com.example.ecommerce_api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Add Product
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // Get All Products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get Product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product Not Found with Id: " + id));
    }

    // Update Product
    public Product updateProduct(Long id, Product updateProduct) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with Id: " + id));
        existing.setName(updateProduct.getName());
        existing.setDescription(updateProduct.getDescription());
        existing.setPrice(updateProduct.getPrice());
        existing.setCategory(updateProduct.getCategory());
        existing.setStock(updateProduct.getStock());

        return productRepository.save(existing);
    }

    // Delete Product
    public void deleteProduct(Long id) {
        productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with Id: " + id));
        productRepository.deleteById(id);
    }

    // Get Products by Category
    public List<Product> getProductByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // Search Products by name
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}