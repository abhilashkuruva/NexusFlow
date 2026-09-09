package com.nexusflow.service;

import com.nexusflow.entity.RiskScore;
import com.nexusflow.entity.Shipment;
import com.nexusflow.entity.Supplier;
import com.nexusflow.repository.DelayPredictionRepository;
import com.nexusflow.repository.RiskScoreRepository;
import com.nexusflow.repository.ShipmentRepository;
import com.nexusflow.repository.SupplierRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {

    @Mock
    private RiskScoreRepository riskScoreRepository;

    @Mock
    private DelayPredictionRepository delayPredictionRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RiskService riskService;

    @Test
    @DisplayName("Should compute and persist risk score for a shipment")
    void testCalculateRiskScore() {
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Alpha Logistics");
        supplier.setTotalShipments(100);
        supplier.setDelayedShipments(10);
        supplier.setReliabilityScore(BigDecimal.valueOf(90.0));
        supplier.setSupplierRiskScore(BigDecimal.valueOf(20.0));

        Shipment shipment = new Shipment();
        shipment.setId(100L);
        shipment.setTrackingNumber("NFS-2026-100");
        shipment.setStatus(Shipment.ShipmentStatus.IN_TRANSIT);
        shipment.setShipmentDate(LocalDate.now().minusDays(2));
        shipment.setEstimatedDeliveryDate(LocalDate.now().plusDays(4));
        shipment.setWeatherImpactScore(BigDecimal.valueOf(2.0));
        shipment.setRouteComplexityScore(BigDecimal.valueOf(2.5));
        shipment.setInventoryRiskScore(BigDecimal.valueOf(1.0));
        shipment.setSupplier(supplier);

        when(shipmentRepository.findById(100L)).thenReturn(Optional.of(shipment));
        when(riskScoreRepository.findByShipmentId(100L)).thenReturn(Optional.empty());
        when(riskScoreRepository.save(any(RiskScore.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RiskScore score = riskService.calculateRiskScore(100L);

        assertNotNull(score);
        assertNotNull(score.getOverallScore());
        assertNotNull(score.getRiskLevel());
        assertNotNull(score.getSupplierRisk());
        assertEquals(shipment, score.getShipment());
    }

    @Test
    @DisplayName("Should throw exception if shipment does not exist")
    void testCalculateRiskScoreShipmentNotFound() {
        when(shipmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            riskService.calculateRiskScore(999L);
        });
    }
}
