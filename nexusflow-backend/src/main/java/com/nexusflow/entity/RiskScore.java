package com.nexusflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a risk score calculation for a shipment.
 * 
 * Risk scores are calculated based on various factors including
 * supplier reliability, shipment history, cargo type, and route
 * complexity. The score helps identify high-risk shipments that
 * may need attention.
 * 
 * @author NexusFlow Team
 */
@Entity
@Table(name = "risk_scores")
public class RiskScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false, unique = true)
    private Shipment shipment;

    @Column(name = "risk_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(name = "risk_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(name = "delay_probability", nullable = false, precision = 5, scale = 2)
    private BigDecimal delayProbability;

    @Column(columnDefinition = "TEXT")
    private String factors;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum representing risk level categories.
     */
    public enum RiskLevel {
        LOW,        // 0-25: Minimal risk
        MEDIUM,     // 26-50: Moderate risk
        HIGH,       // 51-75: High risk
        CRITICAL    // 76-100: Critical risk
    }

    // Constructors
    public RiskScore() {
        this.riskScore = BigDecimal.ZERO;
        this.delayProbability = BigDecimal.ZERO;
        this.riskLevel = RiskLevel.LOW;
    }

    public RiskScore(Shipment shipment, BigDecimal riskScore, BigDecimal delayProbability, 
                     RiskLevel riskLevel, String factors) {
        this();
        this.shipment = shipment;
        this.riskScore = riskScore;
        this.delayProbability = delayProbability;
        this.riskLevel = riskLevel;
        this.factors = factors;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Checks if this risk score represents a high-risk shipment.
     */
    public boolean isHighRisk() {
        return riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL;
    }

    @Override
    public String toString() {
        return "RiskScore{" +
                "id=" + id +
                ", riskLevel=" + riskLevel +
                ", riskScore=" + riskScore +
                ", delayProbability=" + delayProbability +
                '}';
    }
}