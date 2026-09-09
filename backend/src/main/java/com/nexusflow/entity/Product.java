package com.nexusflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku", unique = true, nullable = false)
    private String sku;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @Column(name = "weight_per_unit")
    private BigDecimal weightPerUnit;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Shipment> shipments;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Inventory> inventory;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<DemandHistory> demandHistory;

    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public BigDecimal getWeightPerUnit() { return weightPerUnit; }
    public void setWeightPerUnit(BigDecimal weightPerUnit) { this.weightPerUnit = weightPerUnit; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Shipment> getShipments() { return shipments; }
    public void setShipments(List<Shipment> shipments) { this.shipments = shipments; }

    public List<Inventory> getInventory() { return inventory; }
    public void setInventory(List<Inventory> inventory) { this.inventory = inventory; }

    public List<DemandHistory> getDemandHistory() { return demandHistory; }
    public void setDemandHistory(List<DemandHistory> demandHistory) { this.demandHistory = demandHistory; }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}