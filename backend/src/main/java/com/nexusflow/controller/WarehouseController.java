package com.nexusflow.controller;

import com.nexusflow.entity.Warehouse;
import com.nexusflow.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/warehouses")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Tag(name = "Warehouses", description = "Warehouse management APIs")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping
    @Operation(summary = "Get all warehouses")
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get warehouse by name")
    public ResponseEntity<Warehouse> getWarehouseByName(@PathVariable String name) {
        return warehouseService.getWarehouseByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "Get warehouses by country")
    public ResponseEntity<List<Warehouse>> getWarehousesByCountry(@PathVariable String country) {
        List<Warehouse> warehouses = warehouseService.getWarehousesByCountry(country);
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/countries")
    @Operation(summary = "Get all warehouse countries")
    public ResponseEntity<List<String>> getAllCountries() {
        List<String> countries = warehouseService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/search")
    @Operation(summary = "Search warehouses")
    public ResponseEntity<List<Warehouse>> searchWarehouses(@RequestParam String q) {
        List<Warehouse> warehouses = warehouseService.searchWarehouses(q);
        return ResponseEntity.ok(warehouses);
    }

    @PostMapping
    @Operation(summary = "Create new warehouse")
    public ResponseEntity<Warehouse> createWarehouse(@RequestBody Warehouse warehouse) {
        Warehouse created = warehouseService.createWarehouse(warehouse);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update warehouse")
    public ResponseEntity<Warehouse> updateWarehouse(
            @PathVariable Long id,
            @RequestBody Warehouse warehouseDetails
    ) {
        Warehouse updated = warehouseService.updateWarehouse(id, warehouseDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete warehouse")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/summary")
    @Operation(summary = "Get warehouse statistics summary")
    public ResponseEntity<Map<String, Object>> getWarehouseStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalWarehouses", warehouseService.countActiveWarehouses());
        stats.put("totalCapacity", warehouseService.getTotalCapacity());
        stats.put("countries", warehouseService.getAllCountries());
        return ResponseEntity.ok(stats);
    }
}