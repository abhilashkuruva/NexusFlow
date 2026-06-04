package com.nexusflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a supplier/company in the supply chain.
 * 
 * Suppliers provide goods/services and have associated shipments.
 * The reliability score and shipment statistics are used for risk
 * analysis and delay predictions.
 * 
 * @author NexusFlow Team
 */
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    private String email;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false)
    private String country;

    @Column(name = "reliability_score", precision = 3, scale = 2)
    private BigDecimal reliabilityScore;

    @Column(name = "total_shipments")
    private Integer totalShipments;

    @Column(name = "delayed_shipments")
    private Integer delayedShipments;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Shipment> shipments = new ArrayList<>();

    // Constructors
    public Supplier() {
        this.reliabilityScore = BigDecimal.valueOf(5.00);
        this.totalShipments = 0;
        this.delayedShipments = 0;
        this.isActive = true;
        this.shipments = new ArrayList<>();
    }

    public Supplier(String name, String contactPerson, String email, String phone, 
                    String address, String country) {
        this();
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.country = country;
    }

    // Getters and Setters
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    /**
     * Calculates the delay rate as a percentage.
     */
    public double getDelayRate() {
        if (totalShipments == null || totalShipments == 0) {
            return 0.0;
        }
        return (double) delayedShipments / totalShipments * 100.0;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", reliabilityScore=" + reliabilityScore +
                '}';
    }
}