package com.nexusflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Supplier entity - used for API requests and responses.
 * 
 * @author NexusFlow Team
 */
public class SupplierDTO {

    private Long id;

    @NotBlank(message = "Supplier name is required")
    private String name;

    private String contactPerson;
    private String companyName;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String address;

    @NotBlank(message = "Country is required")
    private String country;

    private String location;
    private String industry;
    private BigDecimal reliabilityScore;
    private BigDecimal deliveryPerformanceScore;
    private BigDecimal qualityScore;
    private BigDecimal financialStabilityScore;
    private BigDecimal geopoliticalExposureScore;
    private BigDecimal historicalReliabilityScore;
    private BigDecimal supplierRiskScore;
    private Integer totalShipments;
    private Integer delayedShipments;
    private String riskLevel;
    private Double delayRate;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public SupplierDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

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

    public Double getDelayRate() { return delayRate; }
    public void setDelayRate(Double delayRate) { this.delayRate = delayRate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}