package com.nexusflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "delay_predictions")
public class DelayPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", unique = true, nullable = false)
    private Shipment shipment;

    @Column(name = "predicted_delay_hours")
    private Integer predictedDelayHours;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "prediction_reason")
    private String predictionReason;

    @Column(name = "is_delayed")
    private Boolean isDelayed;

    @Column(name = "predicted_at")
    private LocalDateTime predictedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DelayPrediction() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }

    public Integer getPredictedDelayHours() { return predictedDelayHours; }
    public void setPredictedDelayHours(Integer predictedDelayHours) { this.predictedDelayHours = predictedDelayHours; }

    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getPredictionReason() { return predictionReason; }
    public void setPredictionReason(String predictionReason) { this.predictionReason = predictionReason; }

    public Boolean getIsDelayed() { return isDelayed; }
    public void setIsDelayed(Boolean isDelayed) { this.isDelayed = isDelayed; }

    public LocalDateTime getPredictedAt() { return predictedAt; }
    public void setPredictedAt(LocalDateTime predictedAt) { this.predictedAt = predictedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}