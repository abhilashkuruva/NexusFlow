package com.nexusflow.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "demand_history")
public class DemandHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "month_date", nullable = false)
    private LocalDate monthDate;

    @Column(name = "actual_demand", nullable = false)
    private Integer actualDemand;

    @Column(name = "predicted_demand")
    private Integer predictedDemand;

    public DemandHistory() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDate getMonthDate() {
        return monthDate;
    }

    public void setMonthDate(LocalDate monthDate) {
        this.monthDate = monthDate;
    }

    public Integer getActualDemand() {
        return actualDemand;
    }

    public void setActualDemand(Integer actualDemand) {
        this.actualDemand = actualDemand;
    }

    public Integer getPredictedDemand() {
        return predictedDemand;
    }

    public void setPredictedDemand(Integer predictedDemand) {
        this.predictedDemand = predictedDemand;
    }
}