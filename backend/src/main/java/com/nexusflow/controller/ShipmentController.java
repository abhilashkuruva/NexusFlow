package com.nexusflow.controller;

import com.nexusflow.dto.ShipmentDTO;
import com.nexusflow.entity.Shipment;
import com.nexusflow.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<ShipmentDTO>> getAllShipments(Pageable pageable) {
        Page<Shipment> shipments = shipmentService.findAll(pageable);
        return ResponseEntity.ok(shipments.map(this::convertToDTO));
    }

    /**
     * Gets a shipment by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Shipment by ID", description = "Retrieve a shipment by its ID")
    public ResponseEntity<ShipmentDTO> getShipmentById(@PathVariable Long id) {
        Shipment shipment = shipmentService.findById(id)
                .orElse(null);

        if (shipment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToDTO(shipment));
    }

    /**
     * Gets a shipment by tracking number.
     */
    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Get Shipment by Tracking Number", 
               description = "Retrieve a shipment by its tracking number")
    public ResponseEntity<ShipmentDTO> getShipmentByTrackingNumber(
            @PathVariable String trackingNumber) {
        Shipment shipment = shipmentService.findByTrackingNumber(trackingNumber);

        if (shipment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToDTO(shipment));
    }

    /**
     * Searches shipments.
     */
    @GetMapping("/search")
    @Operation(summary = "Search Shipments", description = "Search shipments by tracking number or route")
    public ResponseEntity<List<ShipmentDTO>> searchShipments(
            @RequestParam String query) {
        List<Shipment> shipments = shipmentService.searchShipments(query);
        return ResponseEntity.ok(convertToDTOList(shipments));
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
            return ResponseEntity.ok(convertToDTOList(shipments));
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
    public ResponseEntity<List<ShipmentDTO>> getDelayedShipments() {
        List<Shipment> shipments = shipmentService.findDelayedShipments();
        return ResponseEntity.ok(convertToDTOList(shipments));
    }

    /**
     * Gets shipments by supplier.
     */
    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get Shipments by Supplier", description = "Retrieve shipments for a specific supplier")
    public ResponseEntity<List<ShipmentDTO>> getShipmentsBySupplier(
            @PathVariable Long supplierId) {
        List<Shipment> shipments = shipmentService.findBySupplierId(supplierId);
        return ResponseEntity.ok(convertToDTOList(shipments));
    }

    /**
     * Gets shipments by date range.
     */
    @GetMapping("/date-range")
    @Operation(summary = "Get Shipments by Date Range", 
               description = "Retrieve shipments within a date range")
    public ResponseEntity<List<ShipmentDTO>> getShipmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Shipment> shipments = shipmentService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(convertToDTOList(shipments));
    }

    /**
     * Creates a new shipment.
     */
    @PostMapping
    @Operation(summary = "Create Shipment", description = "Create a new shipment")
    public ResponseEntity<?> createShipment(
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

            return ResponseEntity.ok(convertToDTO(shipment));
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
    public ResponseEntity<?> updateShipment(
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

            return ResponseEntity.ok(convertToDTO(shipment));
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
    public ResponseEntity<?> updateShipmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusData) {
        try {
            String status = statusData.get("status");
            Shipment.ShipmentStatus shipmentStatus = Shipment.ShipmentStatus.valueOf(status.toUpperCase());
            Shipment shipment = shipmentService.updateStatus(id, shipmentStatus);
            return ResponseEntity.ok(convertToDTO(shipment));
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
        stats.put("pending", countByStatus(Shipment.ShipmentStatus.PENDING));
        stats.put("inTransit", countByStatus(Shipment.ShipmentStatus.IN_TRANSIT));
        stats.put("delayed", countByStatus(Shipment.ShipmentStatus.DELAYED));
        stats.put("delivered", countByStatus(Shipment.ShipmentStatus.DELIVERED));
        stats.put("cancelled", countByStatus(Shipment.ShipmentStatus.CANCELLED));
        return ResponseEntity.ok(stats);
    }

    // Helper methods
    private List<ShipmentDTO> convertToDTOList(List<Shipment> shipments) {
        return shipments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ShipmentDTO convertToDTO(Shipment shipment) {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(shipment.getId());
        dto.setTrackingNumber(shipment.getTrackingNumber());

        if (shipment.getSupplier() != null) {
            dto.setSupplierId(shipment.getSupplier().getId());
            dto.setSupplierName(shipment.getSupplier().getName());
        }

        dto.setOriginCity(shipment.getOriginCity());
        dto.setOriginCountry(shipment.getOriginCountry());
        dto.setDestinationCity(shipment.getDestinationCity());
        dto.setDestinationCountry(shipment.getDestinationCountry());
        dto.setCurrentLocation(shipment.getCurrentLocation());
        dto.setQuantity(shipment.getQuantity());
        dto.setShipmentDate(shipment.getShipmentDate());
        dto.setEstimatedDeliveryDate(shipment.getEstimatedDeliveryDate());
        dto.setActualDeliveryDate(shipment.getActualDeliveryDate());
        dto.setStatus(shipment.getStatus() != null ? shipment.getStatus().name() : null);
        dto.setCargoType(shipment.getCargoType());
        dto.setWeightKg(shipment.getWeightKg());
        dto.setValueUsd(shipment.getValueUsd());
        dto.setPriority(shipment.getPriority() != null ? shipment.getPriority().name() : null);
        dto.setWeatherImpactScore(shipment.getWeatherImpactScore());
        dto.setRouteComplexityScore(shipment.getRouteComplexityScore());
        dto.setInventoryRiskScore(shipment.getInventoryRiskScore());
        dto.setNotes(shipment.getNotes());
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setUpdatedAt(shipment.getUpdatedAt());
        return dto;
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
        return shipmentService.countByStatus(status);
    }
}
