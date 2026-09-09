package com.nexusflow.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "horizon_days", nullable = false)
    private Integer horizonDays;

    @Column(name = "related_supplier_id")
    private Long relatedSupplierId;

    @Column(name = "related_shipment_id")
    private Long relatedShipmentId;

    @Column(name = "related_product_id")
    private Long relatedProductId;

    @Column(name = "probability_pct", precision = 5, scale = 2, nullable = false)
    private BigDecimal probabilityPct;

    @Enumerated(EnumType.STRING)
    @Column(name = "prediction_class", nullable = false, length = 64)
    private PredictionClass predictionClass;

    @Column(name = "confidence_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal confidenceScore;

    @Column(name = "explanation", columnDefinition = "TEXT", nullable = false)
    private String explanation;

    @Column(name = "prediction_reason", columnDefinition = "TEXT")
    private String predictionReason;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    public enum PredictionClass {
        PRODUCTION_DELAY,
        SHIPMENT_DELAY,
        INVENTORY_SHORTAGE,
        SUPPLIER_FAILURE,
        GEOPOLITICAL_DISRUPTION
    }

    public Prediction() {}

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getHorizonDays() { return horizonDays; }
    public void setHorizonDays(Integer horizonDays) { this.horizonDays = horizonDays; }

    public Long getRelatedSupplierId() { return relatedSupplierId; }
    public void setRelatedSupplierId(Long relatedSupplierId) { this.relatedSupplierId = relatedSupplierId; }

    public Long getRelatedShipmentId() { return relatedShipmentId; }
    public void setRelatedShipmentId(Long relatedShipmentId) { this.relatedShipmentId = relatedShipmentId; }

    public Long getRelatedProductId() { return relatedProductId; }
    public void setRelatedProductId(Long relatedProductId) { this.relatedProductId = relatedProductId; }

    public BigDecimal getProbabilityPct() { return probabilityPct; }
    public void setProbabilityPct(BigDecimal probabilityPct) { this.probabilityPct = probabilityPct; }

    public PredictionClass getPredictionClass() { return predictionClass; }
    public void setPredictionClass(PredictionClass predictionClass) { this.predictionClass = predictionClass; }

    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getPredictionReason() { return predictionReason; }
    public void setPredictionReason(String predictionReason) { this.predictionReason = predictionReason; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}

