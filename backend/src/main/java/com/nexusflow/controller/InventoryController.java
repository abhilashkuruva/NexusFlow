package com.nexusflow.controller;

import com.nexusflow.entity.Inventory;
import com.nexusflow.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:4173"})
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get all inventory items")
    public ResponseEntity<List<Map<String, Object>>> getAllInventory() {
        return ResponseEntity.ok(convertToMapList(inventoryService.getAllInventory()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory item by ID")
    public ResponseEntity<Map<String, Object>> getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id)
                .map(item -> ResponseEntity.ok(convertToMap(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory by product ID")
    public ResponseEntity<List<Map<String, Object>>> getInventoryByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(convertToMapList(inventoryService.getInventoryByProduct(productId)));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get inventory by warehouse ID")
    public ResponseEntity<List<Map<String, Object>>> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(convertToMapList(inventoryService.getInventoryByWarehouse(warehouseId)));
    }

    @PostMapping
    @Operation(summary = "Create new inventory item")
    public ResponseEntity<Map<String, Object>> createInventory(@RequestBody Inventory inventory) {
        Inventory created = inventoryService.createInventory(inventory);
        return ResponseEntity.ok(convertToMap(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update inventory item")
    public ResponseEntity<Map<String, Object>> updateInventory(
            @PathVariable Long id,
            @RequestBody Inventory inventoryDetails
    ) {
        Inventory updated = inventoryService.updateInventory(id, inventoryDetails);
        return ResponseEntity.ok(convertToMap(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inventory item")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items")
    public ResponseEntity<List<Map<String, Object>>> getLowStockItems() {
        return ResponseEntity.ok(convertToMapList(inventoryService.getLowStockItems()));
    }

    @GetMapping("/reorder-needed")
    @Operation(summary = "Get items that need reorder")
    public ResponseEntity<List<Map<String, Object>>> getReorderNeededItems() {
        return ResponseEntity.ok(convertToMapList(inventoryService.getReorderNeededItems()));
    }

    @GetMapping("/overstock")
    @Operation(summary = "Get overstock items")
    public ResponseEntity<List<Map<String, Object>>> getOverstockItems() {
        return ResponseEntity.ok(convertToMapList(inventoryService.getOverstockItems()));
    }

    @GetMapping("/search")
    @Operation(summary = "Search inventory")
    public ResponseEntity<List<Map<String, Object>>> searchInventory(@RequestParam String q) {
        return ResponseEntity.ok(convertToMapList(inventoryService.searchInventory(q)));
    }

    @GetMapping("/stats/summary")
    @Operation(summary = "Get inventory statistics summary")
    public ResponseEntity<Map<String, Object>> getInventoryStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalItems", inventoryService.countActiveInventoryItems());
        stats.put("lowStockItems", inventoryService.countLowStockItems());
        stats.put("totalValue", inventoryService.getTotalInventoryValue());
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{id}/adjust")
    @Operation(summary = "Adjust stock level")
    public ResponseEntity<Map<String, Object>> adjustStock(
            @PathVariable Long id,
            @RequestBody Map<String, Object> adjustment
    ) {
        Integer quantity = (Integer) adjustment.get("quantity");
        String reason = (String) adjustment.get("reason");
        Inventory updated = inventoryService.adjustStock(id, quantity, reason);
        return ResponseEntity.ok(convertToMap(updated));
    }

    @PostMapping("/{id}/reserve")
    @Operation(summary = "Reserve stock")
    public ResponseEntity<Map<String, Object>> reserveStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> reservation
    ) {
        Integer quantity = reservation.get("quantity");
        Inventory updated = inventoryService.reserveStock(id, quantity);
        return ResponseEntity.ok(convertToMap(updated));
    }

    @PostMapping("/{id}/release-reservation")
    @Operation(summary = "Release reserved stock")
    public ResponseEntity<Map<String, Object>> releaseReservation(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> release
    ) {
        Integer quantity = release.get("quantity");
        Inventory updated = inventoryService.releaseReservation(id, quantity);
        return ResponseEntity.ok(convertToMap(updated));
    }

    private List<Map<String, Object>> convertToMapList(List<Inventory> inventory) {
        return inventory.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertToMap(Inventory item) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.getId());
        map.put("availableQuantity", item.getAvailableQuantity());
        map.put("reservedQuantity", item.getReservedQuantity());
        map.put("minimumStockLevel", item.getMinimumStockLevel());
        map.put("maximumStockLevel", item.getMaximumStockLevel());
        map.put("reorderPoint", item.getReorderPoint());
        map.put("unitCost", item.getUnitCost());
        map.put("totalValue", item.getTotalValue());
        map.put("lastStockCheck", item.getLastStockCheck());
        map.put("isActive", item.getIsActive());
        map.put("createdAt", item.getCreatedAt());
        map.put("updatedAt", item.getUpdatedAt());

        if (item.getProduct() != null) {
            map.put("productId", item.getProduct().getId());
            map.put("productName", item.getProduct().getProductName());
            map.put("sku", item.getProduct().getSku());
            map.put("category", item.getProduct().getCategory());
            map.put("unitOfMeasure", item.getProduct().getUnitOfMeasure());
        } else {
            map.put("productId", null);
            map.put("productName", null);
            map.put("sku", null);
            map.put("category", null);
            map.put("unitOfMeasure", null);
        }

        if (item.getWarehouse() != null) {
            map.put("warehouseId", item.getWarehouse().getId());
            map.put("warehouseName", item.getWarehouse().getWarehouseName());
            map.put("warehouseLocation", item.getWarehouse().getLocation());
            map.put("warehouseCountry", item.getWarehouse().getCountry());
            map.put("warehouseCity", item.getWarehouse().getCity());
        } else {
            map.put("warehouseId", null);
            map.put("warehouseName", null);
            map.put("warehouseLocation", null);
            map.put("warehouseCountry", null);
            map.put("warehouseCity", null);
        }

        map.put("isBelowMinimumStock", item.isBelowMinimumStock());
        map.put("needsReorder", item.needsReorder());
        map.put("isAboveMaximumStock", item.isAboveMaximumStock());
        map.put("stockStatus", deriveStockStatus(item));
        map.put("stockRiskLevel", deriveStockRiskLevel(item));
        return map;
    }

    private String deriveStockStatus(Inventory item) {
        if (item.isAboveMaximumStock()) return "OVERSTOCK";
        if (item.needsReorder()) return "REORDER";
        if (item.isBelowMinimumStock()) return "LOW_STOCK";
        return "HEALTHY";
    }

    private String deriveStockRiskLevel(Inventory item) {
        if (item.isAboveMaximumStock()) return "MEDIUM";
        if (item.needsReorder()) return "HIGH";
        if (item.isBelowMinimumStock()) return "HIGH";
        return "LOW";
    }
}
