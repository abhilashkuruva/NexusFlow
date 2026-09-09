package com.nexusflow.service;

import com.nexusflow.entity.Shipment;
import com.nexusflow.entity.Supplier;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * AI Module 1: Shipment Delay Prediction
 * Rule-based model using weighted scoring concept.
 */
public class DelayPredictionModel {

    public static double predictProbability(Shipment shipment, Supplier supplier) {
        double probability = 0.0;

        // 1. Supplier Reliability (Inverse: lower reliability adds risk)
        double reliability = supplier.getReliabilityScore() != null ? supplier.getReliabilityScore().doubleValue() : 3.0;
        probability += (5.0 - reliability) * 10; // Max 20%

        // 2. Previous Delays (Historical delay rate)
        double delayRate = supplier.getTotalShipments() > 0 
            ? (double) supplier.getDelayedShipments() / supplier.getTotalShipments() 
            : 0.0;
        probability += delayRate * 30; // Max 30%

        // 3. Weather Risk (Shipment factor)
        double weatherRisk = shipment.getWeatherImpactScore() != null ? shipment.getWeatherImpactScore().doubleValue() : 0.0;
        probability += weatherRisk * 2.0; // Max 20% if scale is 0-10

        // 4. Current Status
        if (shipment.getStatus() == Shipment.ShipmentStatus.DELAYED) {
            probability += 25.0;
        } else if (shipment.getStatus() == Shipment.ShipmentStatus.IN_TRANSIT) {
            probability += 5.0;
        }

        // 5. Priority Buffer
        if (shipment.getPriority() == Shipment.Priority.URGENT) {
            probability += 10.0; // Urgent shipments often have tighter windows/higher risk
        }

        return Math.min(probability, 100.0);
    }

    public static String determineRiskCategory(double probability) {
        if (probability >= 85) return "CRITICAL";
        if (probability >= 65) return "HIGH";
        if (probability >= 31) return "MEDIUM";
        return "LOW";
    }
}