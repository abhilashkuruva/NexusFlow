package com.nexusflow.repository;

import com.nexusflow.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"product", "warehouse"})
    List<Inventory> findByProductId(Long productId);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"product", "warehouse"})
    List<Inventory> findByWarehouseId(Long warehouseId);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"product", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.isActive = true AND i.availableQuantity <= i.minimumStockLevel")
    List<Inventory> findLowStockItems();

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"product", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.isActive = true AND i.availableQuantity <= i.reorderPoint")
    List<Inventory> findReorderNeededItems();

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"product", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.isActive = true AND i.availableQuantity > i.maximumStockLevel")
    List<Inventory> findOverstockItems();

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"product", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.isActive = true AND i.product.id = :productId AND i.warehouse.id = :warehouseId")
    Optional<Inventory> findByProductAndWarehouse(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);

    @Query("SELECT SUM(i.availableQuantity) FROM Inventory i WHERE i.isActive = true AND i.product.id = :productId")
    Long getTotalAvailableQuantityByProduct(@Param("productId") Long productId);

    @Query("SELECT SUM(i.totalValue) FROM Inventory i WHERE i.isActive = true")
    Long getTotalInventoryValue();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.isActive = true AND i.availableQuantity <= i.minimumStockLevel")
    long countLowStockItems();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.isActive = true")
    long countActiveInventoryItems();

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"product", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.isActive = true ORDER BY i.availableQuantity ASC")
    List<Inventory> findAllActiveOrderByQuantityAsc();

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"product", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.isActive = true ORDER BY i.availableQuantity ASC")
    org.springframework.data.domain.Page<Inventory> findAllActive(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT DISTINCT i.product.category FROM Inventory i WHERE i.isActive = true ORDER BY i.product.category")
    List<String> findAllProductCategories();

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"product", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.isActive = true AND (:searchTerm IS NULL OR LOWER(i.product.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(i.product.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(i.warehouse.warehouseName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(i.warehouse.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Inventory> searchInventory(@Param("searchTerm") String searchTerm);
}