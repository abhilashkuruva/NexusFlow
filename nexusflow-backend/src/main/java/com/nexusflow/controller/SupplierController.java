package com.nexusflow.controller;

import com.nexusflow.entity.Supplier;
import com.nexusflow.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<Map<String, Object>>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.findAll();
        return ResponseEntity.ok(convertToMapList(suppliers));
    }

    /**
     * Gets a supplier by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Supplier by ID", description = "Retrieve a supplier by its ID")
    public ResponseEntity<Map<String, Object>> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.findById(id)
                .orElse(null);
        
        if (supplier == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToMap(supplier));
    }

    /**
     * Searches suppliers by name.
     */
    @GetMapping("/search")
    @Operation(summary = "Search Suppliers", description = "Search suppliers by name")
    public ResponseEntity<List<Map<String, Object>>> searchSuppliers(
            @RequestParam String query) {
        List<Supplier> suppliers = supplierService.searchByName(query);
        return ResponseEntity.ok(convertToMapList(suppliers));
    }

    /**
     * Gets suppliers by country.
     */
    @GetMapping("/country/{country}")
    @Operation(summary = "Get Suppliers by Country", 
               description = "Retrieve suppliers from a specific country")
    public ResponseEntity<List<Map<String, Object>>> getSuppliersByCountry(
            @PathVariable String country) {
        List<Supplier> suppliers = supplierService.findByCountry(country);
        return ResponseEntity.ok(convertToMapList(suppliers));
    }

    /**
     * Gets suppliers with low reliability scores.
     */
    @GetMapping("/low-reliability")
    @Operation(summary = "Get Low Reliability Suppliers", 
               description = "Retrieve suppliers with reliability below threshold")
    public ResponseEntity<List<Map<String, Object>>> getLowReliabilitySuppliers(
            @RequestParam(defaultValue = "3.5") Double threshold) {
        List<Supplier> suppliers = supplierService.findLowReliabilitySuppliers(threshold);
        return ResponseEntity.ok(convertToMapList(suppliers));
    }

    /**
     * Creates a new supplier.
     */
    @PostMapping
    @Operation(summary = "Create Supplier", description = "Create a new supplier")
    public ResponseEntity<Map<String, Object>> createSupplier(
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
            
            return ResponseEntity.ok(convertToMap(supplier));
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
    public ResponseEntity<Map<String, Object>> updateSupplier(
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
            
            return ResponseEntity.ok(convertToMap(supplier));
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
    private List<Map<String, Object>> convertToMapList(List<Supplier> suppliers) {
        return suppliers.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertToMap(Supplier supplier) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", supplier.getId());
        map.put("name", supplier.getName());
        map.put("contactPerson", supplier.getContactPerson());
        map.put("email", supplier.getEmail());
        map.put("phone", supplier.getPhone());
        map.put("address", supplier.getAddress());
        map.put("country", supplier.getCountry());
        map.put("reliabilityScore", supplier.getReliabilityScore());
        map.put("totalShipments", supplier.getTotalShipments());
        map.put("delayedShipments", supplier.getDelayedShipments());
        map.put("delayRate", supplier.getDelayRate());
        map.put("isActive", supplier.getIsActive());
        map.put("createdAt", supplier.getCreatedAt());
        return map;
    }

    private String getString(Map<String, Object> map, String key) {
        return map.get(key) != null ? map.get(key).toString() : null;
    }
}