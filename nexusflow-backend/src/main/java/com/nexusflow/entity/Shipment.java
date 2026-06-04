package com.nexusflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a shipment in the supply chain.
 * 
 * Shipments are the core tracking units in the system, containing
 * information about origin, destination, dates, cargo details,
 * and current status. Each shipment is associated with a supplier.
 * 
 * @author NexusFlow Team
 */
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", nullable = false, unique = true)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "origin_city", nullable = false)
    private String originCity;

    @Column(name = "origin_country", nullable = false)
    private String originCountry;

    @Column(name = "destination_city", nullable = false)
    private String destinationCity;

    @Column(name = "destination_country", nullable = false)
    private String destinationCountry;

    @Column(name = "shipment_date", nullable = false)
    private LocalDate shipmentDate;

    @Column(name = "estimated_delivery_date", nullable = false)
    private LocalDate estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @Column(name = "cargo_type")
    private String cargoType;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "value_usd", precision = 12, scale = 2)
    private BigDecimal valueUsd;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShipmentRoute> routes = new ArrayList<>();

    @OneToOne(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RiskScore riskScore;

    @OneToOne(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DelayPrediction delayPrediction;

    /**
     * Enum representing the current status of a shipment.
     */
    public enum ShipmentStatus {
        PENDING,      // Shipment not yet dispatched
        IN_TRANSIT,   // Shipment is on the way
        DELAYED,      // Shipment is delayed
        DELIVERED,    // Shipment delivered successfully
        CANCELLED     // Shipment cancelled
    }

    /**
     * Enum representing shipment priority levels.
     */
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    // Constructors
    public Shipment() {
        this.status = ShipmentStatus.PENDING;
        this.priority = Priority.MEDIUM;
        this.routes = new ArrayList<>();
    }

    public Shipment(String trackingNumber, Supplier supplier, String originCity, String originCountry,
                    String destinationCity, String destinationCountry, LocalDate shipmentDate,
                    LocalDate estimatedDeliveryDate) {
        this();
        this.trackingNumber = trackingNumber;
        this.supplier = supplier;
        this.originCity = originCity;
        this.originCountry = originCountry;
        this.destinationCity = destinationCity;
        this.destinationCountry = destinationCountry;
        this.shipmentDate = shipmentDate;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public LocalDate getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDate shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDate getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getValueUsd() {
        return valueUsd;
    }

    public void setValueUsd(BigDecimal valueUsd) {
        this.valueUsd = valueUsd;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ShipmentRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<ShipmentRoute> routes) {
        this.routes = routes;
    }

    public RiskScore getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(RiskScore riskScore) {
        this.riskScore = riskScore;
    }

    public DelayPrediction getDelayPrediction() {
        return delayPrediction;
    }

    public void setDelayPrediction(DelayPrediction delayPrediction) {
        this.delayPrediction = delayPrediction;
    }

    /**
     * Checks if the shipment is delayed based on estimated delivery date.
     */
    public boolean isDelayed() {
        return LocalDate.now().isAfter(estimatedDeliveryDate) && 
               actualDeliveryDate == null &&
               (status == ShipmentStatus.IN_TRANSIT || status == ShipmentStatus.PENDING);
    }

    /**
     * Checks if the shipment has been delivered.
     */
    public boolean isDelivered() {
        return status == ShipmentStatus.DELIVERED || actualDeliveryDate != null;
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "id=" + id +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", status=" + status +
                ", originCity='" + originCity + '\'' +
                ", destinationCity='" + destinationCity + '\'' +
                '}';
    }
}