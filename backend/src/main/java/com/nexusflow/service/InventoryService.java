package com.nexusflow.service;

import com.nexusflow.entity.Inventory;
import com.nexusflow.entity.Product;
import com.nexusflow.entity.Warehouse;
import com.nexusflow.repository.InventoryRepository;
import com.nexusflow.repository.ProductRepository;
import com.nexusflow.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAllActiveOrderByQuantityAsc();
    }

    public org.springframework.data.domain.Page<Inventory> getAllInventory(org.springframework.data.domain.Pageable pageable) {
        return inventoryRepository.findAllActive(pageable);
    }

    public List<Inventory> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public List<Inventory> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId);
    }

    public Optional<Inventory> getInventoryById(Long id) {
        return inventoryRepository.findById(id);
    }

    public Inventory createInventory(Inventory inventory) {
        if (inventory.getAvailableQuantity() == null) {
            inventory.setAvailableQuantity(0);
        }
        if (inventory.getReservedQuantity() == null) {
            inventory.setReservedQuantity(0);
        }
        if (inventory.getMinimumStockLevel() == null) {
            inventory.setMinimumStockLevel(10);
        }
        if (inventory.getMaximumStockLevel() == null) {
            inventory.setMaximumStockLevel(1000);
        }
        if (inventory.getReorderPoint() == null) {
            inventory.setReorderPoint(inventory.getMinimumStockLevel() + 5);
        }

        // Calculate total value
        if (inventory.getUnitCost() != null) {
            inventory.setTotalValue(inventory.getUnitCost()
                    .multiply(BigDecimal.valueOf(inventory.getAvailableQuantity())));
        }

        inventory.setLastStockCheck(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    public Inventory updateInventory(Long id, Inventory inventoryDetails) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(id);
        if (inventoryOpt.isEmpty()) {
            throw new RuntimeException("Inventory item not found");
        }

        Inventory inventory = inventoryOpt.get();

        if (inventoryDetails.getAvailableQuantity() != null) {
            inventory.setAvailableQuantity(inventoryDetails.getAvailableQuantity());
        }
        if (inventoryDetails.getReservedQuantity() != null) {
            inventory.setReservedQuantity(inventoryDetails.getReservedQuantity());
        }
        if (inventoryDetails.getMinimumStockLevel() != null) {
            inventory.setMinimumStockLevel(inventoryDetails.getMinimumStockLevel());
        }
        if (inventoryDetails.getMaximumStockLevel() != null) {
            inventory.setMaximumStockLevel(inventoryDetails.getMaximumStockLevel());
        }
        if (inventoryDetails.getReorderPoint() != null) {
            inventory.setReorderPoint(inventoryDetails.getReorderPoint());
        }
        if (inventoryDetails.getUnitCost() != null) {
            inventory.setUnitCost(inventoryDetails.getUnitCost());
            inventory.setTotalValue(inventoryDetails.getUnitCost()
                    .multiply(BigDecimal.valueOf(inventory.getAvailableQuantity())));
        }

        inventory.setLastStockCheck(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException("Inventory item not found");
        }
        inventoryRepository.deleteById(id);
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    public List<Inventory> getReorderNeededItems() {
        return inventoryRepository.findReorderNeededItems();
    }

    public List<Inventory> getOverstockItems() {
        return inventoryRepository.findOverstockItems();
    }

    public List<Inventory> searchInventory(String searchTerm) {
        return inventoryRepository.searchInventory(searchTerm);
    }

    public long countLowStockItems() {
        return inventoryRepository.countLowStockItems();
    }

    public long countActiveInventoryItems() {
        return inventoryRepository.countActiveInventoryItems();
    }

    public Long getTotalInventoryValue() {
        Long value = inventoryRepository.getTotalInventoryValue();
        return value != null ? value : 0L;
    }

    public boolean isBelowMinimumStock(Long inventoryId) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        return inventoryOpt.map(Inventory::isBelowMinimumStock).orElse(false);
    }

    public boolean needsReorder(Long inventoryId) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        return inventoryOpt.map(Inventory::needsReorder).orElse(false);
    }

    public Inventory adjustStock(Long inventoryId, Integer adjustment, String reason) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        if (inventoryOpt.isEmpty()) {
            throw new RuntimeException("Inventory item not found");
        }

        Inventory inventory = inventoryOpt.get();
        int newQuantity = inventory.getAvailableQuantity() + adjustment;
        if (newQuantity < 0) {
            throw new RuntimeException("Cannot reduce stock below zero");
        }

        inventory.setAvailableQuantity(newQuantity);
        inventory.setLastStockCheck(LocalDateTime.now());

        // Update total value
        if (inventory.getUnitCost() != null) {
            inventory.setTotalValue(inventory.getUnitCost()
                    .multiply(BigDecimal.valueOf(newQuantity)));
        }

        return inventoryRepository.save(inventory);
    }

    public Inventory reserveStock(Long inventoryId, Integer quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        if (inventoryOpt.isEmpty()) {
            throw new RuntimeException("Inventory item not found");
        }

        Inventory inventory = inventoryOpt.get();
        if (inventory.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for reservation");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventory.setLastStockCheck(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public Inventory releaseReservation(Long inventoryId, Integer quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        if (inventoryOpt.isEmpty()) {
            throw new RuntimeException("Inventory item not found");
        }

        Inventory inventory = inventoryOpt.get();
        if (inventory.getReservedQuantity() < quantity) {
            throw new RuntimeException("Reserved quantity insufficient");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setLastStockCheck(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }
}