package com.nexusflow.repository;

import com.nexusflow.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByCategory(String category);

    List<Product> findByIsActiveTrueOrderByProductNameAsc();

    List<Product> findByCategoryAndIsActiveTrue(String category);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true ORDER BY p.category")
    List<String> findAllCategories();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (:searchTerm IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Product> searchProducts(@Param("searchTerm") String searchTerm);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    long countActiveProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category AND p.isActive = true")
    long countByCategory(@Param("category") String category);
}