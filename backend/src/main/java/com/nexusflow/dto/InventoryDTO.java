package com.nexusflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryDTO {

    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;
    private String productName;
    private String sku;
    private String category;
    private String unitOfMeasure;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;
    private String warehouseName;
    private String warehouseLocation;
    private String warehouseCountry;
    private String warehouseCity;

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer availableQuantity;

    @Min(value = 0, message = "Reserved quantity cannot be negative")
    private Integer reservedQuantity;

    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minimumStockLevel;

    private Integer maximumStockLevel;
    private Integer reorderPoint;
    private BigDecimal unitCost;
    private BigDecimal totalValue;
    private LocalDateTime lastStockCheck;
    private BigDecimal shortageProbabilityPct;
    private Integer safetyStockLevel;
    private String shortageRiskLevel;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    private Boolean isBelowMinimumStock;
    private Boolean needsReorder;
    private Boolean isAboveMaximumStock;
    private String stockStatus;
    private String stockRiskLevel;

    public InventoryDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }

    public String getWarehouseCountry() { return warehouseCountry; }
    public void setWarehouseCountry(String warehouseCountry) { this.warehouseCountry = warehouseCountry; }

    public String getWarehouseCity() { return warehouseCity; }
    public void setWarehouseCity(String warehouseCity) { this.warehouseCity = warehouseCity; }

    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public Integer getMinimumStockLevel() { return minimumStockLevel; }
    public void setMinimumStockLevel(Integer minimumStockLevel) { this.minimumStockLevel = minimumStockLevel; }

    public Integer getMaximumStockLevel() { return maximumStockLevel; }
    public void setMaximumStockLevel(Integer maximumStockLevel) { this.maximumStockLevel = maximumStockLevel; }

    public Integer getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(Integer reorderPoint) { this.reorderPoint = reorderPoint; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public LocalDateTime getLastStockCheck() { return lastStockCheck; }
    public void setLastStockCheck(LocalDateTime lastStockCheck) { this.lastStockCheck = lastStockCheck; }

    public BigDecimal getShortageProbabilityPct() { return shortageProbabilityPct; }
    public void setShortageProbabilityPct(BigDecimal shortageProbabilityPct) { this.shortageProbabilityPct = shortageProbabilityPct; }

    public Integer getSafetyStockLevel() { return safetyStockLevel; }
    public void setSafetyStockLevel(Integer safetyStockLevel) { this.safetyStockLevel = safetyStockLevel; }

    public String getShortageRiskLevel() { return shortageRiskLevel; }
    public void setShortageRiskLevel(String shortageRiskLevel) { this.shortageRiskLevel = shortageRiskLevel; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public Boolean getIsBelowMinimumStock() { return isBelowMinimumStock; }
    public void setIsBelowMinimumStock(Boolean belowMinimumStock) { isBelowMinimumStock = belowMinimumStock; }

    public Boolean getNeedsReorder() { return needsReorder; }
    public void setNeedsReorder(Boolean needsReorder) { this.needsReorder = needsReorder; }

    public Boolean getIsAboveMaximumStock() { return isAboveMaximumStock; }
    public void setIsAboveMaximumStock(Boolean aboveMaximumStock) { isAboveMaximumStock = aboveMaximumStock; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public String getStockRiskLevel() { return stockRiskLevel; }
    public void setStockRiskLevel(String stockRiskLevel) { this.stockRiskLevel = stockRiskLevel; }
}
