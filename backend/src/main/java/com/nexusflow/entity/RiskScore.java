package com.nexusflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_scores")
public class RiskScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", unique = true, nullable = false)
    private Shipment shipment;

    @Column(name = "supplier_risk", precision = 5, scale = 2)
    private BigDecimal supplierRisk;

    @Column(name = "delay_probability", precision = 5, scale = 2)
    private BigDecimal delayProbability;

    @Column(name = "weather_risk", precision = 5, scale = 2)
    private BigDecimal weatherRisk;

    @Column(name = "route_risk", precision = 5, scale = 2)
    private BigDecimal routeRisk;

    @Column(name = "inventory_risk", precision = 5, scale = 2)
    private BigDecimal inventoryRisk;

    @Column(name = "overall_score", precision = 5, scale = 2)
    private BigDecimal overallScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "generated_time")
    private LocalDateTime generatedTime;

    @Column(name = "factors")
    private String factors; // To store a summary of contributing factors

    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public RiskScore() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }

    public BigDecimal getSupplierRisk() { return supplierRisk; }
    public void setSupplierRisk(BigDecimal supplierRisk) { this.supplierRisk = supplierRisk; }

    public BigDecimal getDelayProbability() { return delayProbability; }
    public void setDelayProbability(BigDecimal delayProbability) { this.delayProbability = delayProbability; }

    public BigDecimal getWeatherRisk() { return weatherRisk; }
    public void setWeatherRisk(BigDecimal weatherRisk) { this.weatherRisk = weatherRisk; }

    public BigDecimal getRouteRisk() { return routeRisk; }
    public void setRouteRisk(BigDecimal routeRisk) { this.routeRisk = routeRisk; }

    public BigDecimal getInventoryRisk() { return inventoryRisk; }
    public void setInventoryRisk(BigDecimal inventoryRisk) { this.inventoryRisk = inventoryRisk; }

    public BigDecimal getOverallScore() { return overallScore; }
    public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }

    public BigDecimal getRiskScore() {
        return overallScore;
    }

    public LocalDateTime getCalculatedAt() {
        return generatedTime;
    }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public LocalDateTime getGeneratedTime() { return generatedTime; }
    public void setGeneratedTime(LocalDateTime generatedTime) { this.generatedTime = generatedTime; }

    public String getFactors() { return factors; }
    public void setFactors(String factors) { this.factors = factors; }
}