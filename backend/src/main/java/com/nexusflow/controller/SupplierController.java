package com.nexusflow.controller;

import com.nexusflow.dto.SupplierDTO;
import com.nexusflow.entity.Supplier;
import com.nexusflow.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for supplier operations.
 * 
 * Handles CRUD operations for suppliers and performance analysis.
 * 
 * @author NexusFlow Team
 */
@RestController
@RequestMapping("/suppliers")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Suppliers", description = "Supplier management APIs")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * Gets all suppliers.
     */
    @GetMapping
    @Operation(summary = "Get All Suppliers", description = "Retrieve all suppliers")
    public ResponseEntity<Page<SupplierDTO>> getAllSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierService.findAll(pageable);
        return ResponseEntity.ok(suppliers.map(this::convertToDTO));
    }

    /**
     * Gets a supplier by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Supplier by ID", description = "Retrieve a supplier by its ID")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.findById(id)
                .orElse(null);
        
        if (supplier == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToDTO(supplier));
    }

    /**
     * Searches suppliers by name.
     */
    @GetMapping("/search")
    @Operation(summary = "Search Suppliers", description = "Search suppliers by name")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(
            @RequestParam String query) {
        List<Supplier> suppliers = supplierService.searchByName(query);
        return ResponseEntity.ok(convertToDTOList(suppliers));
    }

    /**
     * Gets suppliers by country.
     */
    @GetMapping("/country/{country}")
    @Operation(summary = "Get Suppliers by Country", 
               description = "Retrieve suppliers from a specific country")
    public ResponseEntity<List<SupplierDTO>> getSuppliersByCountry(
            @PathVariable String country) {
        List<Supplier> suppliers = supplierService.findByCountry(country);
        return ResponseEntity.ok(convertToDTOList(suppliers));
    }

    /**
     * Gets suppliers with low reliability scores.
     */
    @GetMapping("/low-reliability")
    @Operation(summary = "Get Low Reliability Suppliers", 
               description = "Retrieve suppliers with reliability below threshold")
    public ResponseEntity<List<SupplierDTO>> getLowReliabilitySuppliers(
            @RequestParam(defaultValue = "3.5") Double threshold) {
        List<Supplier> suppliers = supplierService.findLowReliabilitySuppliers(threshold);
        return ResponseEntity.ok(convertToDTOList(suppliers));
    }

    /**
     * Creates a new supplier.
     */
    @PostMapping
    @Operation(summary = "Create Supplier", description = "Create a new supplier")
    public ResponseEntity<?> createSupplier(
            @RequestBody Map<String, Object> supplierData) {
        try {
            Supplier supplier = supplierService.createSupplier(
                    getString(supplierData, "name"),
                    getString(supplierData, "contactPerson"),
                    getString(supplierData, "email"),
                    getString(supplierData, "phone"),
                    getString(supplierData, "address"),
                    getString(supplierData, "country")
            );
            
            return ResponseEntity.ok(convertToDTO(supplier));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to create supplier: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Updates a supplier.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Supplier", description = "Update an existing supplier")
    public ResponseEntity<?> updateSupplier(
            @PathVariable Long id,
            @RequestBody Map<String, Object> supplierData) {
        try {
            Supplier supplier = supplierService.updateSupplier(
                    id,
                    getString(supplierData, "name"),
                    getString(supplierData, "contactPerson"),
                    getString(supplierData, "email"),
                    getString(supplierData, "phone"),
                    getString(supplierData, "address"),
                    getString(supplierData, "country")
            );
            
            return ResponseEntity.ok(convertToDTO(supplier));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to update supplier: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Deletes a supplier.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Supplier", description = "Delete a supplier (soft delete)")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deactivateSupplier(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets supplier statistics.
     */
    @GetMapping("/stats/summary")
    @Operation(summary = "Get Supplier Statistics", 
               description = "Get summary statistics for suppliers")
    public ResponseEntity<Map<String, Object>> getSupplierStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", supplierService.countSuppliers());
        stats.put("averageReliability", supplierService.getAverageReliabilityScore());
        return ResponseEntity.ok(stats);
    }

    /**
     * Gets suppliers grouped by country.
     */
    @GetMapping("/stats/by-country")
    @Operation(summary = "Get Suppliers by Country", 
               description = "Get count of suppliers grouped by country")
    public ResponseEntity<List<Map<String, Object>>> getSuppliersByCountryStats() {
        List<Object[]> results = supplierService.getSuppliersByCountry();
        List<Map<String, Object>> stats = results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("country", result[0]);
                    map.put("count", result[1]);
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(stats);
    }

    // Helper methods
    private List<SupplierDTO> convertToDTOList(List<Supplier> suppliers) {
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SupplierDTO convertToDTO(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setCompanyName(supplier.getCompanyName());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setAddress(supplier.getAddress());
        dto.setCountry(supplier.getCountry());
        dto.setLocation(supplier.getLocation());
        dto.setIndustry(supplier.getIndustry());
        dto.setReliabilityScore(supplier.getReliabilityScore());
        dto.setDeliveryPerformanceScore(supplier.getDeliveryPerformanceScore());
        dto.setQualityScore(supplier.getQualityScore());
        dto.setFinancialStabilityScore(supplier.getFinancialStabilityScore());
        dto.setGeopoliticalExposureScore(supplier.getGeopoliticalExposureScore());
        dto.setHistoricalReliabilityScore(supplier.getHistoricalReliabilityScore());
        dto.setSupplierRiskScore(supplier.getSupplierRiskScore());
        dto.setTotalShipments(supplier.getTotalShipments());
        dto.setDelayedShipments(supplier.getDelayedShipments());
        dto.setRiskLevel(supplier.getRiskLevel());
        dto.setDelayRate(supplier.getDelayRate());
        dto.setIsActive(supplier.getIsActive());
        dto.setCreatedAt(supplier.getCreatedAt());
        return dto;
    }

    private String getString(Map<String, Object> map, String key) {
        return map.get(key) != null ? map.get(key).toString() : null;
    }
}
