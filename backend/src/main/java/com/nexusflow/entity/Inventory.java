package com.nexusflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity;

    @Column(name = "minimum_stock_level", nullable = false)
    private Integer minimumStockLevel;

    @Column(name = "maximum_stock_level")
    private Integer maximumStockLevel;

    @Column(name = "reorder_point")
    private Integer reorderPoint;

    @Column(name = "unit_cost", precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_value", precision = 14, scale = 2)
    private BigDecimal totalValue;

    @Column(name = "last_stock_check")
    private LocalDateTime lastStockCheck;

    @Column(name = "shortage_probability_pct", precision = 5, scale = 2)
    private BigDecimal shortageProbabilityPct;

    @Column(name = "safety_stock_level")
    private Integer safetyStockLevel;

    @Column(name = "shortage_risk_level", length = 20)
    private String shortageRiskLevel;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")

    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public Inventory() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.reservedQuantity = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }

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

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isBelowMinimumStock() {
        return this.availableQuantity != null && 
               this.minimumStockLevel != null && 
               this.availableQuantity < this.minimumStockLevel;
    }

    public boolean isAboveMaximumStock() {
        return this.availableQuantity != null && 
               this.maximumStockLevel != null && 
               this.availableQuantity > this.maximumStockLevel;
    }

    public boolean needsReorder() {
        return this.availableQuantity != null && 
               this.reorderPoint != null && 
               this.availableQuantity <= this.reorderPoint;
    }
}