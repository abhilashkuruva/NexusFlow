package com.nexusflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", unique = true, nullable = false)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "origin_city", nullable = false)
    private String originCity;

    @Column(name = "origin_country", nullable = false)
    private String originCountry;

    @Column(name = "destination_city", nullable = false)
    private String destinationCity;

    @Column(name = "destination_country", nullable = false)
    private String destinationCountry;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "current_location")
    private String currentLocation;

    @Column(name = "shipment_date", nullable = false)
    private LocalDate shipmentDate;

    @Column(name = "estimated_delivery_date", nullable = false)
    private LocalDate estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @Column(name = "cargo_type")
    private String cargoType;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "value_usd", precision = 12, scale = 2)
    private BigDecimal valueUsd;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    @Column(name = "weather_impact_score", precision = 5, scale = 2)
    private BigDecimal weatherImpactScore;

    @Column(name = "route_complexity_score", precision = 5, scale = 2)
    private BigDecimal routeComplexityScore;

    @Column(name = "inventory_risk_score", precision = 5, scale = 2)
    private BigDecimal inventoryRiskScore;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ShipmentStatus {
        CREATED, IN_TRANSIT, DELAYED, DELIVERED, CANCELLED, PENDING
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public Shipment() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public String getDestinationCountry() { return destinationCountry; }
    public void setDestinationCountry(String destinationCountry) { this.destinationCountry = destinationCountry; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public LocalDate getShipmentDate() { return shipmentDate; }
    public void setShipmentDate(LocalDate shipmentDate) { this.shipmentDate = shipmentDate; }

    public LocalDate getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }

    public LocalDate getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDate actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }

    public String getCargoType() { return cargoType; }
    public void setCargoType(String cargoType) { this.cargoType = cargoType; }

    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }

    public BigDecimal getValueUsd() { return valueUsd; }
    public void setValueUsd(BigDecimal valueUsd) { this.valueUsd = valueUsd; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public BigDecimal getWeatherImpactScore() { return weatherImpactScore; }
    public void setWeatherImpactScore(BigDecimal weatherImpactScore) { this.weatherImpactScore = weatherImpactScore; }

    public BigDecimal getRouteComplexityScore() { return routeComplexityScore; }
    public void setRouteComplexityScore(BigDecimal routeComplexityScore) { this.routeComplexityScore = routeComplexityScore; }

    public BigDecimal getInventoryRiskScore() { return inventoryRiskScore; }
    public void setInventoryRiskScore(BigDecimal inventoryRiskScore) { this.inventoryRiskScore = inventoryRiskScore; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isDelayed() {
        if (this.status == ShipmentStatus.DELAYED) {
            return true;
        }
        if (this.estimatedDeliveryDate != null && this.estimatedDeliveryDate.isBefore(LocalDate.now()) && this.actualDeliveryDate == null) {
            return true;
        }
        return false;
    }
}