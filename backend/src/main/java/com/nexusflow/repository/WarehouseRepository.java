package com.nexusflow.repository;

import com.nexusflow.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByWarehouseName(String warehouseName);

    boolean existsByWarehouseName(String warehouseName);

    List<Warehouse> findByCountry(String country);

    List<Warehouse> findByIsActiveTrueOrderByWarehouseNameAsc();

    List<Warehouse> findByCountryAndIsActiveTrue(String country);

    @Query("SELECT DISTINCT w.country FROM Warehouse w WHERE w.isActive = true ORDER BY w.country")
    List<String> findAllCountries();

    @Query("SELECT w FROM Warehouse w WHERE w.isActive = true AND (:searchTerm IS NULL OR LOWER(w.warehouseName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(w.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(w.country) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Warehouse> searchWarehouses(@Param("searchTerm") String searchTerm);

    @Query("SELECT COUNT(w) FROM Warehouse w WHERE w.isActive = true")
    long countActiveWarehouses();

    @Query("SELECT COUNT(w) FROM Warehouse w WHERE w.country = :country AND w.isActive = true")
    long countByCountry(@Param("country") String country);

    @Query("SELECT SUM(w.capacity) FROM Warehouse w WHERE w.isActive = true")
    Long getTotalCapacity();
}