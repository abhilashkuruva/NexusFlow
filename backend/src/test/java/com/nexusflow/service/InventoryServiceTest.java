package com.nexusflow.service;

import com.nexusflow.entity.Inventory;
import com.nexusflow.repository.InventoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("Should successfully reserve stock when available")
    void testReserveStockSuccess() {
        Inventory inventory = new Inventory();
        inventory.setId(10L);
        inventory.setAvailableQuantity(100);
        inventory.setReservedQuantity(10);

        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventory result = inventoryService.reserveStock(10L, 20);

        assertNotNull(result);
        assertEquals(80, result.getAvailableQuantity());
        assertEquals(30, result.getReservedQuantity());
    }

    @Test
    @DisplayName("Should throw exception when reserving more than available stock")
    void testReserveStockInsufficient() {
        Inventory inventory = new Inventory();
        inventory.setId(10L);
        inventory.setAvailableQuantity(15);
        inventory.setReservedQuantity(0);

        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(inventory));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            inventoryService.reserveStock(10L, 50);
        });

        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    @DisplayName("Should adjust stock and recalculate total value")
    void testAdjustStock() {
        Inventory inventory = new Inventory();
        inventory.setId(5L);
        inventory.setAvailableQuantity(50);
        inventory.setUnitCost(BigDecimal.valueOf(10.0));

        when(inventoryRepository.findById(5L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventory result = inventoryService.adjustStock(5L, 20, "Restock batch");

        assertEquals(70, result.getAvailableQuantity());
        assertEquals(BigDecimal.valueOf(700.0), result.getTotalValue());
    }
}
