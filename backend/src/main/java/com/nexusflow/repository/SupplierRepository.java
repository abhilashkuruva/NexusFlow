package com.nexusflow.repository;

import com.nexusflow.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    default List<Supplier> findTopPerformingSuppliers(int limit) {
        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "reliabilityScore")
        );
        return findByIsActiveTrue(pageable).getContent();
    }

    Page<Supplier> findByIsActiveTrue(Pageable pageable);

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
