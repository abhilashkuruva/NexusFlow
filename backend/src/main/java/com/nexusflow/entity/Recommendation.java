package com.nexusflow.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type", nullable = false, length = 80)
    private RecommendationType recommendationType;

    @Column(name = "target_entity_type", nullable = false, length = 80)
    private String targetEntityType;

    @Column(name = "target_entity_id")
    private Long targetEntityId;

    @Column(name = "recommendation_text", columnDefinition = "TEXT", nullable = false)
    private String recommendationText;

    // stored as JSON in DB
    @Column(name = "parameters", columnDefinition = "JSON")
    private String parameters;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RecommendationType {
        SWITCH_SUPPLIER_ALLOCATION,
        INCREASE_SAFETY_STOCK,
        CHANGE_TRANSPORT_ROUTE,
        ACTIVATE_BACKUP_SUPPLIER,
        OTHER
    }

    public enum Status { ACTIVE, STALE, IMPLEMENTED, DISMISSED }

    public Recommendation() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RecommendationType getRecommendationType() { return recommendationType; }
    public void setRecommendationType(RecommendationType recommendationType) { this.recommendationType = recommendationType; }

    public String getTargetEntityType() { return targetEntityType; }
    public void setTargetEntityType(String targetEntityType) { this.targetEntityType = targetEntityType; }

    public Long getTargetEntityId() { return targetEntityId; }
    public void setTargetEntityId(Long targetEntityId) { this.targetEntityId = targetEntityId; }

    public String getRecommendationText() { return recommendationText; }
    public void setRecommendationText(String recommendationText) { this.recommendationText = recommendationText; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

