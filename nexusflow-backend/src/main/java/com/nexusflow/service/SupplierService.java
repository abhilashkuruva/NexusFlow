package com.nexusflow.service;

import com.nexusflow.entity.Supplier;
import com.nexusflow.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Supplier-related business logic.
 * 
 * Handles supplier CRUD operations and performance analysis.
 * 
 * @author NexusFlow Team
 */
@Service
@Transactional
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    /**
     * Finds a supplier by ID.
     */
    public Optional<Supplier> findById(Long id) {
        return supplierRepository.findById(id);
    }

    /**
     * Finds all suppliers.
     */
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    /**
     * Finds all active suppliers.
     */
    public List<Supplier> findActiveSuppliers() {
        return supplierRepository.findByIsActiveTrue();
    }

    /**
     * Finds suppliers by country.
     */
    public List<Supplier> findByCountry(String country) {
        return supplierRepository.findByCountry(country);
    }

    /**
     * Finds suppliers with low reliability scores.
     */
    public List<Supplier> findLowReliabilitySuppliers(Double threshold) {
        return supplierRepository.findByReliabilityScoreLessThanEqual(threshold);
    }

    /**
     * Searches suppliers by name.
     */
    public List<Supplier> searchByName(String searchTerm) {
        return supplierRepository.searchByName(searchTerm);
    }

    /**
     * Creates a new supplier.
     */
    public Supplier createSupplier(String name, String contactPerson, String email, 
                                   String phone, String address, String country) {
        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setContactPerson(contactPerson);
        supplier.setEmail(email);
        supplier.setPhone(phone);
        supplier.setAddress(address);
        supplier.setCountry(country);

        return supplierRepository.save(supplier);
    }

    /**
     * Updates an existing supplier.
     */
    public Supplier updateSupplier(Long id, String name, String contactPerson, 
                                   String email, String phone, String address, 
                                   String country) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);
        
        if (supplierOpt.isEmpty()) {
            throw new RuntimeException("Supplier not found");
        }

        Supplier supplier = supplierOpt.get();
        
        if (name != null) {
            supplier.setName(name);
        }
        if (contactPerson != null) {
            supplier.setContactPerson(contactPerson);
        }
        if (email != null) {
            supplier.setEmail(email);
        }
        if (phone != null) {
            supplier.setPhone(phone);
        }
        if (address != null) {
            supplier.setAddress(address);
        }
        if (country != null) {
            supplier.setCountry(country);
        }

        return supplierRepository.save(supplier);
    }

    /**
     * Deactivates a supplier (soft delete).
     */
    public void deactivateSupplier(Long id) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);
        
        if (supplierOpt.isPresent()) {
            Supplier supplier = supplierOpt.get();
            supplier.setIsActive(false);
            supplierRepository.save(supplier);
        }
    }

    /**
     * Updates supplier reliability score based on performance.
     */
    public void updateReliabilityScore(Long id, Double newScore) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);
        
        if (supplierOpt.isPresent()) {
            Supplier supplier = supplierOpt.get();
            supplier.setReliabilityScore(java.math.BigDecimal.valueOf(newScore));
            supplierRepository.save(supplier);
        }
    }

    /**
     * Gets supplier statistics.
     */
    public long countSuppliers() {
        return supplierRepository.count();
    }

    /**
     * Gets average reliability score across all suppliers.
     */
    public Double getAverageReliabilityScore() {
        return supplierRepository.getAverageReliabilityScore();
    }

    /**
     * Gets top performing suppliers.
     */
    public List<Supplier> getTopPerformingSuppliers(int limit) {
        return supplierRepository.findTopPerformingSuppliers(limit);
    }

    /**
     * Gets suppliers grouped by country with counts.
     */
    public List<Object[]> getSuppliersByCountry() {
        return supplierRepository.countSuppliersByCountry();
    }
}