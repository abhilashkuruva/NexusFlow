package com.nexusflow.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Supplier entity - used for API requests and responses.
 * 
 * @author NexusFlow Team
 */
public class SupplierDTO {

    private Long id;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String country;
    private BigDecimal reliabilityScore;
    private Integer totalShipments;
    private Integer delayedShipments;
    private Double delayRate;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public SupplierDTO() {
    }

    public SupplierDTO(Long id, String name, String contactPerson, String email, 
                       String phone, String address, String country, 
                       BigDecimal reliabilityScore, Integer totalShipments, 
                       Integer delayedShipments, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.country = country;
        this.reliabilityScore = reliabilityScore;
        this.totalShipments = totalShipments;
        this.delayedShipments = delayedShipments;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.delayRate = calculateDelayRate();
    }

    private Double calculateDelayRate() {
        if (totalShipments == null || totalShipments == 0) {
            return 0.0;
        }
        return (double) delayedShipments / totalShipments * 100.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public BigDecimal getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(BigDecimal reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public Integer getTotalShipments() {
        return totalShipments;
    }

    public void setTotalShipments(Integer totalShipments) {
        this.totalShipments = totalShipments;
    }

    public Integer getDelayedShipments() {
        return delayedShipments;
    }

    public void setDelayedShipments(Integer delayedShipments) {
        this.delayedShipments = delayedShipments;
    }

    public Double getDelayRate() {
        return delayRate;
    }

    public void setDelayRate(Double delayRate) {
        this.delayRate = delayRate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}