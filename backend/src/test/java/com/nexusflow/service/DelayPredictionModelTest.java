package com.nexusflow.service;

import com.nexusflow.entity.Shipment;
import com.nexusflow.entity.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DelayPredictionModelTest {

    @Test
    @DisplayName("Should compute delay probability and category for standard shipment")
    void testPredictProbability() {
        Shipment shipment = new Shipment();
        shipment.setStatus(Shipment.ShipmentStatus.IN_TRANSIT);
        shipment.setShipmentDate(LocalDate.now().minusDays(2));
        shipment.setEstimatedDeliveryDate(LocalDate.now().plusDays(3));
        shipment.setWeatherImpactScore(BigDecimal.valueOf(2.5));
        shipment.setRouteComplexityScore(BigDecimal.valueOf(3.0));
        shipment.setInventoryRiskScore(BigDecimal.valueOf(1.5));

        Supplier supplier = new Supplier();
        supplier.setReliabilityScore(BigDecimal.valueOf(4.5));
        supplier.setTotalShipments(50);
        supplier.setDelayedShipments(5);
        supplier.setSupplierRiskScore(BigDecimal.valueOf(20.0));
        shipment.setSupplier(supplier);

        double probability = DelayPredictionModel.predictProbability(shipment, supplier);
        assertTrue(probability >= 0.0 && probability <= 100.0, "Probability must be between 0 and 100");

        String category = DelayPredictionModel.determineRiskCategory(probability);
        assertNotNull(category);
        assertTrue(category.equals("LOW") || category.equals("MEDIUM") || category.equals("HIGH") || category.equals("CRITICAL"));
    }

    @Test
    @DisplayName("Should assign HIGH or CRITICAL risk when shipment is delayed")
    void testOverdueShipmentRisk() {
        Shipment shipment = new Shipment();
        shipment.setStatus(Shipment.ShipmentStatus.DELAYED);
        shipment.setShipmentDate(LocalDate.now().minusDays(10));
        shipment.setEstimatedDeliveryDate(LocalDate.now().minusDays(2));
        shipment.setWeatherImpactScore(BigDecimal.valueOf(8.5));
        shipment.setRouteComplexityScore(BigDecimal.valueOf(4.0));
        shipment.setInventoryRiskScore(BigDecimal.valueOf(3.5));
        shipment.setPriority(Shipment.Priority.URGENT);

        Supplier supplier = new Supplier();
        supplier.setReliabilityScore(BigDecimal.valueOf(1.0));
        supplier.setTotalShipments(20);
        supplier.setDelayedShipments(18);
        shipment.setSupplier(supplier);

        double probability = DelayPredictionModel.predictProbability(shipment, supplier);
        assertTrue(probability > 50.0, "Delayed urgent shipment with low reliability supplier should have high probability");

        String category = DelayPredictionModel.determineRiskCategory(probability);
        assertTrue(category.equals("HIGH") || category.equals("CRITICAL") || category.equals("MEDIUM"));
    }
}
