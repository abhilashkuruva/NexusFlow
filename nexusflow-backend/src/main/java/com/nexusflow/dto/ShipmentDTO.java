package com.nexusflow.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Shipment entity - used for API requests and responses.
 * 
 * @author NexusFlow Team
 */
public class ShipmentDTO {

    private Long id;
    private String trackingNumber;
    private Long supplierId;
    private String supplierName;
    private String originCity;
    private String originCountry;
    private String destinationCity;
    private String destinationCountry;
    private LocalDate shipmentDate;
    private LocalDate estimatedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private String status;
    private String cargoType;
    private BigDecimal weightKg;
    private BigDecimal valueUsd;
    private String priority;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Risk and prediction fields (for detailed view)
    private String riskLevel;
    private BigDecimal riskScore;
    private Boolean predictedDelayed;
    private Integer predictedDelayHours;

    public ShipmentDTO() {
    }

    public ShipmentDTO(Long id, String trackingNumber, Long supplierId, String supplierName,
                       String originCity, String originCountry, String destinationCity, 
                       String destinationCountry, LocalDate shipmentDate,
                       LocalDate estimatedDeliveryDate, LocalDate actualDeliveryDate,
                       String status, String cargoType, BigDecimal weightKg, BigDecimal valueUsd,
                       String priority, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.trackingNumber = trackingNumber;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.originCity = originCity;
        this.originCountry = originCountry;
        this.destinationCity = destinationCity;
        this.destinationCountry = destinationCountry;
        this.shipmentDate = shipmentDate;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.status = status;
        this.cargoType = cargoType;
        this.weightKg = weightKg;
        this.valueUsd = valueUsd;
        this.priority = priority;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
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

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }

    public Boolean getPredictedDelayed() {
        return predictedDelayed;
    }

    public void setPredictedDelayed(Boolean predictedDelayed) {
        this.predictedDelayed = predictedDelayed;
    }

    public Integer getPredictedDelayHours() {
        return predictedDelayHours;
    }

    public void setPredictedDelayHours(Integer predictedDelayHours) {
        this.predictedDelayHours = predictedDelayHours;
    }
}