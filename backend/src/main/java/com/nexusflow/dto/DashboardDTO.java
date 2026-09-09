package com.nexusflow.dto;

import java.util.List;
import java.util.Map;

public class DashboardDTO {
    private long totalShipments;
    private long activeShipments;
    private long delayedShipments;
    private long highRiskSuppliers;
    private double avgDeliveryTime;
    private List<Map<String, Object>> riskDistribution;

    public DashboardDTO() {}

    public long getTotalShipments() { return totalShipments; }
    public void setTotalShipments(long totalShipments) { this.totalShipments = totalShipments; }

    public long getActiveShipments() { return activeShipments; }
    public void setActiveShipments(long activeShipments) { this.activeShipments = activeShipments; }

    public long getDelayedShipments() { return delayedShipments; }
    public void setDelayedShipments(long delayedShipments) { this.delayedShipments = delayedShipments; }

    public long getHighRiskSuppliers() { return highRiskSuppliers; }
    public void setHighRiskSuppliers(long highRiskSuppliers) { this.highRiskSuppliers = highRiskSuppliers; }

    public double getAvgDeliveryTime() { return avgDeliveryTime; }
    public void setAvgDeliveryTime(double avgDeliveryTime) { this.avgDeliveryTime = avgDeliveryTime; }

    public List<Map<String, Object>> getRiskDistribution() { return riskDistribution; }
    public void setRiskDistribution(List<Map<String, Object>> riskDistribution) { this.riskDistribution = riskDistribution; }
}