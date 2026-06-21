package com.nexusflow.repository;

import com.nexusflow.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Supplier entity operations.
 * 
 * Provides CRUD operations and custom query methods for
 * managing supplier data in the database.
 * 
 * @author NexusFlow Team
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Finds suppliers by country.
     */
    List<Supplier> findByCountry(String country);

    /**
     * Finds all active suppliers.
     */
    List<Supplier> findByIsActiveTrue();

    /**
     * Finds suppliers with reliability score below a threshold.
     */
    List<Supplier> findByReliabilityScoreLessThanEqual(Double threshold);

    /**
     * Finds the top performing suppliers by reliability score.
     */
    @Query("SELECT s FROM Supplier s WHERE s.isActive = true ORDER BY s.reliabilityScore DESC LIMIT :limit")
    List<Supplier> findTopPerformingSuppliers(@Param("limit") int limit);

    /**
     * Searches suppliers by name.
     */
    @Query("SELECT s FROM Supplier s WHERE s.isActive = true AND LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Supplier> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Counts total number of suppliers.
     */
    long count();

    /**
     * Counts active suppliers.
     */
    long countByIsActiveTrue();

    /**
     * Groups suppliers by country and counts them.
     */
    @Query("SELECT s.country, COUNT(s) FROM Supplier s WHERE s.isActive = true GROUP BY s.country")
    List<Object[]> countSuppliersByCountry();

    /**
     * Calculates average reliability score.
     */
    @Query("SELECT AVG(s.reliabilityScore) FROM Supplier s WHERE s.isActive = true")
    Double getAverageReliabilityScore();
}