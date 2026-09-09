package com.nexusflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "country")
    private String country;

    @Column(name = "location")
    private String location;

    @Column(name = "industry")
    private String industry;

    @Column(name = "reliability_score", precision = 5, scale = 2)
    private BigDecimal reliabilityScore;

    // PRD supplier intelligence breakdown (0-100 scale components)
    @Column(name = "delivery_performance_score", precision = 5, scale = 2)
    private BigDecimal deliveryPerformanceScore;

    @Column(name = "quality_score", precision = 5, scale = 2)
    private BigDecimal qualityScore;

    @Column(name = "financial_stability_score", precision = 5, scale = 2)
    private BigDecimal financialStabilityScore;

    @Column(name = "geopolitical_exposure_score", precision = 5, scale = 2)
    private BigDecimal geopoliticalExposureScore;

    @Column(name = "historical_reliability_score", precision = 5, scale = 2)
    private BigDecimal historicalReliabilityScore;

    @Column(name = "supplier_risk_score", precision = 5, scale = 2)
    private BigDecimal supplierRiskScore;



    @Column(name = "total_shipments")
    private Integer totalShipments;

    @Column(name = "delayed_shipments")
    private Integer delayedShipments;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive;

    public Supplier() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public BigDecimal getReliabilityScore() { return reliabilityScore; }
    public void setReliabilityScore(BigDecimal reliabilityScore) { this.reliabilityScore = reliabilityScore; }

    public BigDecimal getDeliveryPerformanceScore() { return deliveryPerformanceScore; }
    public void setDeliveryPerformanceScore(BigDecimal deliveryPerformanceScore) { this.deliveryPerformanceScore = deliveryPerformanceScore; }

    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }

    public BigDecimal getFinancialStabilityScore() { return financialStabilityScore; }
    public void setFinancialStabilityScore(BigDecimal financialStabilityScore) { this.financialStabilityScore = financialStabilityScore; }

    public BigDecimal getGeopoliticalExposureScore() { return geopoliticalExposureScore; }
    public void setGeopoliticalExposureScore(BigDecimal geopoliticalExposureScore) { this.geopoliticalExposureScore = geopoliticalExposureScore; }

    public BigDecimal getHistoricalReliabilityScore() { return historicalReliabilityScore; }
    public void setHistoricalReliabilityScore(BigDecimal historicalReliabilityScore) { this.historicalReliabilityScore = historicalReliabilityScore; }

    public BigDecimal getSupplierRiskScore() { return supplierRiskScore; }
    public void setSupplierRiskScore(BigDecimal supplierRiskScore) { this.supplierRiskScore = supplierRiskScore; }



    public Integer getTotalShipments() { return totalShipments; }
    public void setTotalShipments(Integer totalShipments) { this.totalShipments = totalShipments; }

    public Integer getDelayedShipments() { return delayedShipments; }
    public void setDelayedShipments(Integer delayedShipments) { this.delayedShipments = delayedShipments; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    // Helper method to calculate delay rate
    public double getDelayRate() {
        if (totalShipments != null && totalShipments > 0) {
            return (double) delayedShipments / totalShipments * 100.0;
        }
        return 0.0;
    }

    // PRD severity mapping: LOW/MEDIUM/HIGH/CRITICAL
    public String classifySupplierRisk(BigDecimal score) {
        if (score == null) return "LOW";
        double s = score.doubleValue();
        if (s >= 85) return "CRITICAL";
        if (s >= 70) return "HIGH";
        if (s >= 45) return "MEDIUM";
        return "LOW";
    }


}
