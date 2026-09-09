package com.nexusflow.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_events")
public class RiskEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "risk_type", nullable = false, length = 80)
    private String riskType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private Severity severity;

    @Column(name = "affected_entity_type", nullable = false, length = 80)
    private String affectedEntityType;

    @Column(name = "affected_entity_id")
    private Long affectedEntityId;

    @Column(name = "impact_assessment", columnDefinition = "TEXT")
    private String impactAssessment;

    @Column(name = "recommended_action", columnDefinition = "TEXT")
    private String recommendedAction;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

    public enum Status { NEW, ACKNOWLEDGED, IN_PROGRESS, RESOLVED }

    public RiskEvent() {}

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

    public String getRiskType() { return riskType; }
    public void setRiskType(String riskType) { this.riskType = riskType; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getAffectedEntityType() { return affectedEntityType; }
    public void setAffectedEntityType(String affectedEntityType) { this.affectedEntityType = affectedEntityType; }

    public Long getAffectedEntityId() { return affectedEntityId; }
    public void setAffectedEntityId(Long affectedEntityId) { this.affectedEntityId = affectedEntityId; }

    public String getImpactAssessment() { return impactAssessment; }
    public void setImpactAssessment(String impactAssessment) { this.impactAssessment = impactAssessment; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

