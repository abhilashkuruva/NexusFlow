package com.nexusflow.controller;

import com.nexusflow.entity.Shipment;
import com.nexusflow.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for shipment operations.
 * 
 * Handles CRUD operations for shipments, tracking, and status updates.
 * 
 * @author NexusFlow Team
 */
@RestController
@RequestMapping("/shipments")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Shipments", description = "Shipment management APIs")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    /**
     * Gets all shipments.
     */
    @GetMapping
    @Operation(summary = "Get All Shipments", description = "Retrieve all shipments")
    public ResponseEntity<List<Map<String, Object>>> getAllShipments() {
        List<Shipment> shipments = shipmentService.findAll();
        return ResponseEntity.ok(convertToMapList(shipments));
    }

    /**
     * Gets a shipment by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Shipment by ID", description = "Retrieve a shipment by its ID")
    public ResponseEntity<Map<String, Object>> getShipmentById(@PathVariable Long id) {
        Shipment shipment = shipmentService.findById(id)
                .orElse(null);
        
        if (shipment == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToMap(shipment));
    }

    /**
     * Gets a shipment by tracking number.
     */
    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Get Shipment by Tracking Number", 
               description = "Retrieve a shipment by its tracking number")
    public ResponseEntity<Map<String, Object>> getShipmentByTrackingNumber(
            @PathVariable String trackingNumber) {
        Shipment shipment = shipmentService.findByTrackingNumber(trackingNumber);
        
        if (shipment == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToMap(shipment));
    }

    /**
     * Searches shipments.
     */
    @GetMapping("/search")
    @Operation(summary = "Search Shipments", description = "Search shipments by tracking number or route")
    public ResponseEntity<List<Map<String, Object>>> searchShipments(
            @RequestParam String query) {
        List<Shipment> shipments = shipmentService.searchShipments(query);
        return ResponseEntity.ok(convertToMapList(shipments));
    }

    /**
     * Gets shipments by status.
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get Shipments by Status", description = "Retrieve shipments filtered by status")
    public ResponseEntity<?> getShipmentsByStatus(
            @PathVariable String status) {
        try {
            Shipment.ShipmentStatus shipmentStatus = Shipment.ShipmentStatus.valueOf(status.toUpperCase());
            List<Shipment> shipments = shipmentService.findByStatus(shipmentStatus);
            return ResponseEntity.ok(convertToMapList(shipments));
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Invalid status. Use: PENDING, IN_TRANSIT, DELAYED, DELIVERED, CANCELLED");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Gets delayed shipments.
     */
    @GetMapping("/delayed")
    @Operation(summary = "Get Delayed Shipments", description = "Retrieve all delayed shipments")
    public ResponseEntity<List<Map<String, Object>>> getDelayedShipments() {
        List<Shipment> shipments = shipmentService.findDelayedShipments();
        return ResponseEntity.ok(convertToMapList(shipments));
    }

    /**
     * Gets shipments by supplier.
     */
    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get Shipments by Supplier", description = "Retrieve shipments for a specific supplier")
    public ResponseEntity<List<Map<String, Object>>> getShipmentsBySupplier(
            @PathVariable Long supplierId) {
        List<Shipment> shipments = shipmentService.findBySupplierId(supplierId);
        return ResponseEntity.ok(convertToMapList(shipments));
    }

    /**
     * Gets shipments by date range.
     */
    @GetMapping("/date-range")
    @Operation(summary = "Get Shipments by Date Range", 
               description = "Retrieve shipments within a date range")
    public ResponseEntity<List<Map<String, Object>>> getShipmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Shipment> shipments = shipmentService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(convertToMapList(shipments));
    }

    /**
     * Creates a new shipment.
     */
    @PostMapping
    @Operation(summary = "Create Shipment", description = "Create a new shipment")
    public ResponseEntity<Map<String, Object>> createShipment(
            @RequestBody Map<String, Object> shipmentData) {
        try {
            Shipment shipment = shipmentService.createShipment(
                    getLong(shipmentData, "supplierId"),
                    getString(shipmentData, "originCity"),
                    getString(shipmentData, "originCountry"),
                    getString(shipmentData, "destinationCity"),
                    getString(shipmentData, "destinationCountry"),
                    getLocalDate(shipmentData, "shipmentDate"),
                    getLocalDate(shipmentData, "estimatedDeliveryDate"),
                    getString(shipmentData, "cargoType"),
                    getBigDecimal(shipmentData, "weightKg"),
                    getBigDecimal(shipmentData, "valueUsd"),
                    getString(shipmentData, "priority"),
                    getString(shipmentData, "notes")
            );
            
            return ResponseEntity.ok(convertToMap(shipment));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to create shipment: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Updates a shipment.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Shipment", description = "Update an existing shipment")
    public ResponseEntity<Map<String, Object>> updateShipment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> shipmentData) {
        try {
            Shipment shipment = shipmentService.updateShipment(
                    id,
                    getString(shipmentData, "originCity"),
                    getString(shipmentData, "originCountry"),
                    getString(shipmentData, "destinationCity"),
                    getString(shipmentData, "destinationCountry"),
                    getLocalDate(shipmentData, "shipmentDate"),
                    getLocalDate(shipmentData, "estimatedDeliveryDate"),
                    getString(shipmentData, "cargoType"),
                    getBigDecimal(shipmentData, "weightKg"),
                    getBigDecimal(shipmentData, "valueUsd"),
                    getString(shipmentData, "priority"),
                    getString(shipmentData, "notes")
            );
            
            return ResponseEntity.ok(convertToMap(shipment));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to update shipment: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Updates shipment status.
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Shipment Status", description = "Update the status of a shipment")
    public ResponseEntity<Map<String, Object>> updateShipmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusData) {
        try {
            String status = statusData.get("status");
            Shipment.ShipmentStatus shipmentStatus = Shipment.ShipmentStatus.valueOf(status.toUpperCase());
            Shipment shipment = shipmentService.updateStatus(id, shipmentStatus);
            return ResponseEntity.ok(convertToMap(shipment));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to update status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Deletes a shipment.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Shipment", description = "Delete a shipment")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets shipment statistics.
     */
    @GetMapping("/stats/summary")
    @Operation(summary = "Get Shipment Statistics", description = "Get summary statistics for shipments")
    public ResponseEntity<Map<String, Object>> getShipmentStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", shipmentService.countShipments());
        stats.put("pending", (long) shipmentService.findByStatus(Shipment.ShipmentStatus.PENDING).size());
        stats.put("inTransit", (long) shipmentService.findByStatus(Shipment.ShipmentStatus.IN_TRANSIT).size());
        stats.put("delayed", (long) shipmentService.findByStatus(Shipment.ShipmentStatus.DELAYED).size());
        stats.put("delivered", (long) shipmentService.findByStatus(Shipment.ShipmentStatus.DELIVERED).size());
        stats.put("cancelled", (long) shipmentService.findByStatus(Shipment.ShipmentStatus.CANCELLED).size());
        return ResponseEntity.ok(stats);
    }

    // Helper methods
    private List<Map<String, Object>> convertToMapList(List<Shipment> shipments) {
        return shipments.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertToMap(Shipment shipment) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", shipment.getId());
        map.put("trackingNumber", shipment.getTrackingNumber());
        map.put("supplierId", shipment.getSupplier().getId());
        map.put("supplierName", shipment.getSupplier().getName());
        map.put("originCity", shipment.getOriginCity());
        map.put("originCountry", shipment.getOriginCountry());
        map.put("destinationCity", shipment.getDestinationCity());
        map.put("destinationCountry", shipment.getDestinationCountry());
        map.put("shipmentDate", shipment.getShipmentDate());
        map.put("estimatedDeliveryDate", shipment.getEstimatedDeliveryDate());
        map.put("actualDeliveryDate", shipment.getActualDeliveryDate());
        map.put("status", shipment.getStatus().name());
        map.put("cargoType", shipment.getCargoType());
        map.put("weightKg", shipment.getWeightKg());
        map.put("valueUsd", shipment.getValueUsd());
        map.put("priority", shipment.getPriority() != null ? shipment.getPriority().name() : null);
        map.put("notes", shipment.getNotes());
        return map;
    }

    private String getString(Map<String, Object> map, String key) {
        return map.get(key) != null ? map.get(key).toString() : null;
    }

    private Long getLong(Map<String, Object> map, String key) {
        if (map.get(key) == null) return null;
        if (map.get(key) instanceof Number) {
            return ((Number) map.get(key)).longValue();
        }
        return Long.parseLong(map.get(key).toString());
    }

    private java.math.BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        if (map.get(key) == null) return null;
        if (map.get(key) instanceof Number) {
            return java.math.BigDecimal.valueOf(((Number) map.get(key)).doubleValue());
        }
        return new java.math.BigDecimal(map.get(key).toString());
    }

    private LocalDate getLocalDate(Map<String, Object> map, String key) {
        if (map.get(key) == null) return null;
        return LocalDate.parse(map.get(key).toString());
    }

    private long countByStatus(Shipment.ShipmentStatus status) {
        return shipmentService.findByStatus(status).size();
    }
}