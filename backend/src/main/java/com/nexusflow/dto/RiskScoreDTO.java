package com.nexusflow.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for RiskScore entity - used for API responses.
 * 
 * @author NexusFlow Team
 */
public class RiskScoreDTO {

    private Long id;
    private Long shipmentId;
    private String trackingNumber;
    private String riskLevel;
    private BigDecimal riskScore;
    private BigDecimal delayProbability;
    private String factors;
    private LocalDateTime calculatedAt;

    public RiskScoreDTO() {
    }

    public RiskScoreDTO(Long id, Long shipmentId, String trackingNumber, String riskLevel,
                        BigDecimal riskScore, BigDecimal delayProbability, 
                        String factors, LocalDateTime calculatedAt) {
        this.id = id;
        this.shipmentId = shipmentId;
        this.trackingNumber = trackingNumber;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.delayProbability = delayProbability;
        this.factors = factors;
        this.calculatedAt = calculatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
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

    public BigDecimal getDelayProbability() {
        return delayProbability;
    }

    public void setDelayProbability(BigDecimal delayProbability) {
        this.delayProbability = delayProbability;
    }

    public String getFactors() {
        return factors;
    }

    public void setFactors(String factors) {
        this.factors = factors;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}