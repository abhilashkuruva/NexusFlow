package com.nexusflow.service;

import com.nexusflow.entity.Product;
import com.nexusflow.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findByIsActiveTrueOrderByProductNameAsc();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndIsActiveTrue(category);
    }

    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchProducts(searchTerm);
    }

    public Product createProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new RuntimeException("Product with SKU already exists");
        }

        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();

        if (productDetails.getProductName() != null) {
            product.setProductName(productDetails.getProductName());
        }
        if (productDetails.getCategory() != null) {
            product.setCategory(productDetails.getCategory());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getUnitPrice() != null) {
            product.setUnitPrice(productDetails.getUnitPrice());
        }
        if (productDetails.getUnitOfMeasure() != null) {
            product.setUnitOfMeasure(productDetails.getUnitOfMeasure());
        }
        if (productDetails.getWeightPerUnit() != null) {
            product.setWeightPerUnit(productDetails.getWeightPerUnit());
        }
        if (productDetails.getIsActive() != null) {
            product.setIsActive(productDetails.getIsActive());
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public long countActiveProducts() {
        return productRepository.countActiveProducts();
    }

    public long countByCategory(String category) {
        return productRepository.countByCategory(category);
    }
}