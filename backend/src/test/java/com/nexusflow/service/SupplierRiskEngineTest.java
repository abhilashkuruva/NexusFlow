package com.nexusflow.service;

import com.nexusflow.entity.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SupplierRiskEngineTest {

    private SupplierRiskEngine supplierRiskEngine;

    @BeforeEach
    void setUp() {
        supplierRiskEngine = new SupplierRiskEngine();
    }

    @Test
    @DisplayName("Should classify low-risk supplier correctly")
    void testLowRiskSupplier() {
        Supplier supplier = new Supplier();
        supplier.setName("Reliable Supplier");
        supplier.setDeliveryPerformanceScore(BigDecimal.valueOf(10.0));
        supplier.setQualityScore(BigDecimal.valueOf(10.0));
        supplier.setFinancialStabilityScore(BigDecimal.valueOf(10.0));
        supplier.setGeopoliticalExposureScore(BigDecimal.valueOf(5.0));
        supplier.setHistoricalReliabilityScore(BigDecimal.valueOf(5.0));

        SupplierRiskEngine.SupplierRiskResult result = supplierRiskEngine.evaluate(supplier);
        assertNotNull(result);
        assertNotNull(result.riskLevel());
        assertTrue(result.supplierRiskScore().doubleValue() >= 0);
    }

    @Test
    @DisplayName("Should detect high delay rate and elevate supplier risk")
    void testHighDelayRateSupplier() {
        Supplier supplier = new Supplier();
        supplier.setName("At-Risk Supplier");
        supplier.setDeliveryPerformanceScore(BigDecimal.valueOf(25.0));
        supplier.setQualityScore(BigDecimal.valueOf(20.0));
        supplier.setFinancialStabilityScore(BigDecimal.valueOf(20.0));
        supplier.setGeopoliticalExposureScore(BigDecimal.valueOf(15.0));
        supplier.setHistoricalReliabilityScore(BigDecimal.valueOf(15.0));

        SupplierRiskEngine.SupplierRiskResult result = supplierRiskEngine.evaluate(supplier);
        assertNotNull(result);
        assertTrue(result.riskLevel().equals("HIGH") || result.riskLevel().equals("CRITICAL"));
    }

    @Test
    @DisplayName("Should guarantee score stays within 0-100 range when all components are 100")
    void testMax100ScaleComponentsDoNotExceed100() {
        Supplier supplier = new Supplier();
        supplier.setName("Worst Case Supplier");
        supplier.setDeliveryPerformanceScore(BigDecimal.valueOf(100.0));
        supplier.setQualityScore(BigDecimal.valueOf(100.0));
        supplier.setFinancialStabilityScore(BigDecimal.valueOf(100.0));
        supplier.setGeopoliticalExposureScore(BigDecimal.valueOf(100.0));
        supplier.setHistoricalReliabilityScore(BigDecimal.valueOf(100.0));

        SupplierRiskEngine.SupplierRiskResult result = supplierRiskEngine.evaluate(supplier);
        assertNotNull(result);
        assertEquals(100.00, result.supplierRiskScore().doubleValue(), 0.01);
        assertEquals("CRITICAL", result.riskLevel());
    }

    @Test
    @DisplayName("Should correctly normalize weighted component inputs on 0-100 scale")
    void testNormalizedWeightedInputs() {
        Supplier supplier = new Supplier();
        supplier.setName("Moderate Supplier");
        // Delivery: 60 * 0.25 = 15.0
        // Quality: 50 * 0.20 = 10.0
        // Financial: 40 * 0.20 = 8.0
        // Geo: 30 * 0.15 = 4.5
        // Historical: 50 * 0.20 = 10.0
        // Total = 47.50 -> MEDIUM risk
        supplier.setDeliveryPerformanceScore(BigDecimal.valueOf(60.0));
        supplier.setQualityScore(BigDecimal.valueOf(50.0));
        supplier.setFinancialStabilityScore(BigDecimal.valueOf(40.0));
        supplier.setGeopoliticalExposureScore(BigDecimal.valueOf(30.0));
        supplier.setHistoricalReliabilityScore(BigDecimal.valueOf(50.0));

        SupplierRiskEngine.SupplierRiskResult result = supplierRiskEngine.evaluate(supplier);
        assertNotNull(result);
        assertEquals(47.50, result.supplierRiskScore().doubleValue(), 0.01);
        assertEquals("MEDIUM", result.riskLevel());
    }
}
