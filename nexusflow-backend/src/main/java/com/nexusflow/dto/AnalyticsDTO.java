package com.nexusflow.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO for Analytics data - used for dashboard statistics.
 * 
 * @author NexusFlow Team
 */
public class AnalyticsDTO {

    // Shipment statistics
    private Long totalShipments;
    private Long pendingShipments;
    private Long inTransitShipments;
    private Long delayedShipments;
    private Long deliveredShipments;

    // Supplier statistics
    private Long totalSuppliers;
    private Double averageReliabilityScore;

    // Risk statistics
    private Long highRiskShipments;
    private Long criticalRiskShipments;
    private Double averageRiskScore;

    // Delay predictions
    private Long predictedDelays;
    private Double averagePredictedDelayHours;
    private Double averageConfidenceScore;

    // Chart data
    private List<Map<String, Object>> shipmentByStatus;
    private List<Map<String, Object>> shipmentByMonth;
    private List<Map<String, Object>> riskByLevel;
    private List<Map<String, Object>> suppliersByCountry;

    public AnalyticsDTO() {
    }

    // Getters and Setters
    public Long getTotalShipments() {
        return totalShipments;
    }

    public void setTotalShipments(Long totalShipments) {
        this.totalShipments = totalShipments;
    }

    public Long getPendingShipments() {
        return pendingShipments;
    }

    public void setPendingShipments(Long pendingShipments) {
        this.pendingShipments = pendingShipments;
    }

    public Long getInTransitShipments() {
        return inTransitShipments;
    }

    public void setInTransitShipments(Long inTransitShipments) {
        this.inTransitShipments = inTransitShipments;
    }

    public Long getDelayedShipments() {
        return delayedShipments;
    }

    public void setDelayedShipments(Long delayedShipments) {
        this.delayedShipments = delayedShipments;
    }

    public Long getDeliveredShipments() {
        return deliveredShipments;
    }

    public void setDeliveredShipments(Long deliveredShipments) {
        this.deliveredShipments = deliveredShipments;
    }

    public Long getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(Long totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public Double getAverageReliabilityScore() {
        return averageReliabilityScore;
    }

    public void setAverageReliabilityScore(Double averageReliabilityScore) {
        this.averageReliabilityScore = averageReliabilityScore;
    }

    public Long getHighRiskShipments() {
        return highRiskShipments;
    }

    public void setHighRiskShipments(Long highRiskShipments) {
        this.highRiskShipments = highRiskShipments;
    }

    public Long getCriticalRiskShipments() {
        return criticalRiskShipments;
    }

    public void setCriticalRiskShipments(Long criticalRiskShipments) {
        this.criticalRiskShipments = criticalRiskShipments;
    }

    public Double getAverageRiskScore() {
        return averageRiskScore;
    }

    public void setAverageRiskScore(Double averageRiskScore) {
        this.averageRiskScore = averageRiskScore;
    }

    public Long getPredictedDelays() {
        return predictedDelays;
    }

    public void setPredictedDelays(Long predictedDelays) {
        this.predictedDelays = predictedDelays;
    }

    public Double getAveragePredictedDelayHours() {
        return averagePredictedDelayHours;
    }

    public void setAveragePredictedDelayHours(Double averagePredictedDelayHours) {
        this.averagePredictedDelayHours = averagePredictedDelayHours;
    }

    public Double getAverageConfidenceScore() {
        return averageConfidenceScore;
    }

    public void setAverageConfidenceScore(Double averageConfidenceScore) {
        this.averageConfidenceScore = averageConfidenceScore;
    }

    public List<Map<String, Object>> getShipmentByStatus() {
        return shipmentByStatus;
    }

    public void setShipmentByStatus(List<Map<String, Object>> shipmentByStatus) {
        this.shipmentByStatus = shipmentByStatus;
    }

    public List<Map<String, Object>> getShipmentByMonth() {
        return shipmentByMonth;
    }

    public void setShipmentByMonth(List<Map<String, Object>> shipmentByMonth) {
        this.shipmentByMonth = shipmentByMonth;
    }

    public List<Map<String, Object>> getRiskByLevel() {
        return riskByLevel;
    }

    public void setRiskByLevel(List<Map<String, Object>> riskByLevel) {
        this.riskByLevel = riskByLevel;
    }

    public List<Map<String, Object>> getSuppliersByCountry() {
        return suppliersByCountry;
    }

    public void setSuppliersByCountry(List<Map<String, Object>> suppliersByCountry) {
        this.suppliersByCountry = suppliersByCountry;
    }
}