package com.nexusflow.service;

import com.nexusflow.entity.*;
import com.nexusflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Risk Analysis and Delay Prediction business logic.
 * 
 * Implements AI-inspired algorithms for calculating risk scores and
 * predicting shipment delays based on various factors.
 * 
 * @author NexusFlow Team
 */
@Service
@Transactional
public class RiskService {

    @Autowired
    private RiskScoreRepository riskScoreRepository;

    @Autowired
    private DelayPredictionRepository delayPredictionRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    public RiskScore calculateRiskScore(Long shipmentId) {
        Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
        if (shipmentOpt.isEmpty())
            throw new RuntimeException("Shipment not found");

        Shipment shipment = shipmentOpt.get();
        Supplier supplier = shipment.getSupplier();

        // AI Module 1 Logic
        double delayProb = DelayPredictionModel.predictProbability(shipment, supplier);
        String category = DelayPredictionModel.determineRiskCategory(delayProb);

        // AI Module 2: Supplier Risk Score Calculation
        // Formula: (Delay Rate * 40) + (Late Delivery * 30) + (Performance History * 30)
        double delayRate = supplier.getTotalShipments() > 0 
            ? (double) supplier.getDelayedShipments() / supplier.getTotalShipments() : 0.0;
        double lateDeliveryFactor = (shipment.getStatus() == Shipment.ShipmentStatus.DELAYED) ? 1.0 : 0.0;
        double perfHistory = (supplier.getReliabilityScore() != null ? supplier.getReliabilityScore().doubleValue() : 3.0) / 5.0;
        
        double supplierRiskScore = (delayRate * 40) + (lateDeliveryFactor * 30) + ((1.0 - perfHistory) * 30);

        double weatherRisk = shipment.getWeatherImpactScore() != null ? shipment.getWeatherImpactScore().doubleValue() : 0.0;
        double routeRisk = shipment.getRouteComplexityScore() != null ? shipment.getRouteComplexityScore().doubleValue() : 0.0;
        double inventoryRisk = shipment.getInventoryRiskScore() != null ? shipment.getInventoryRiskScore().doubleValue() : 0.0;

        double overallScore = (supplierRiskScore * 0.4) + (weatherRisk * 2) + (routeRisk * 2) + (inventoryRisk * 2);

        RiskScore riskScore;
        riskScore = riskScoreRepository.findByShipmentId(shipmentId).orElse(new RiskScore());
        riskScore.setShipment(shipment);
        riskScore.setSupplierRisk(BigDecimal.valueOf(supplierRiskScore).setScale(2, RoundingMode.HALF_UP));
        riskScore.setWeatherRisk(BigDecimal.valueOf(weatherRisk).setScale(2, RoundingMode.HALF_UP));
        riskScore.setRouteRisk(BigDecimal.valueOf(routeRisk).setScale(2, RoundingMode.HALF_UP));
        riskScore.setInventoryRisk(BigDecimal.valueOf(inventoryRisk).setScale(2, RoundingMode.HALF_UP));
        riskScore.setOverallScore(BigDecimal.valueOf(overallScore).setScale(2, RoundingMode.HALF_UP));
        riskScore.setDelayProbability(BigDecimal.valueOf(delayProb).setScale(2, RoundingMode.HALF_UP));
        riskScore.setRiskLevel(RiskScore.RiskLevel.valueOf(category));
        riskScore.setGeneratedTime(java.time.LocalDateTime.now());

        // Trigger Notification Engine for High Risk
        if ("HIGH".equals(category) || "CRITICAL".equals(category)) {
            notificationService.sendAlertToUsers(
                "Critical Risk Alert", 
                "Shipment " + shipment.getTrackingNumber() + " flagged as " + category, 
                Notification.NotificationType.ALERT,
                User.Role.ADMIN, User.Role.SUPPLY_MANAGER, User.Role.LOGISTICS_MANAGER
            );
        }

        return riskScoreRepository.save(riskScore);
    }

    /**
     * Predicts delay for a shipment.
     * 
     * Prediction factors:
     * - Supplier delay history
     * - Days until delivery
     * - Shipment status
     * - Route complexity
     */
    public DelayPrediction predictDelay(Long shipmentId) {
        Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
        
        if (shipmentOpt.isEmpty()) {
            throw new RuntimeException("Shipment not found");
        }

        Shipment shipment = shipmentOpt.get();
        Supplier supplier = shipment.getSupplier();

        // Base delay probability from supplier history
        double baseProbability = supplier.getDelayRate() / 100.0;

        // Adjust based on days until delivery
        long daysUntilDelivery = ChronoUnit.DAYS.between(LocalDate.now(), 
                                                          shipment.getEstimatedDeliveryDate());
        double timeAdjustment = 0;
        if (daysUntilDelivery < 0) {
            // Already past delivery date
            timeAdjustment = 0.4;
        } else if (daysUntilDelivery < 3) {
            timeAdjustment = 0.2;
        } else if (daysUntilDelivery < 7) {
            timeAdjustment = 0.1;
        }

        // Adjust based on current status
        double statusAdjustment = 0;
        if (shipment.getStatus() == Shipment.ShipmentStatus.DELAYED) {
            statusAdjustment = 0.3;
        } else if (shipment.getStatus() == Shipment.ShipmentStatus.IN_TRANSIT) {
            statusAdjustment = 0.1;
        }

        // Calculate final probability
        double finalProbability = Math.min(baseProbability + timeAdjustment + statusAdjustment, 1.0);

        // Predict delay hours if delayed
        int predictedDelayHours = 0;
        if (finalProbability > 0.5) {
            if (daysUntilDelivery < 0) {
                predictedDelayHours = (int) Math.abs(daysUntilDelivery) * 24 + 24;
            } else {
                predictedDelayHours = (int) (Math.random() * 48 + 12);
            }
        }

        // Confidence score based on data availability
        double confidenceScore = 60.0;
        if (supplier.getTotalShipments() > 100) {
            confidenceScore += 20.0;
        } else if (supplier.getTotalShipments() > 50) {
            confidenceScore += 10.0;
        }
        if (daysUntilDelivery >= 0) {
            confidenceScore += 10.0;
        }

        // Build prediction reason
        StringBuilder reason = new StringBuilder();
        reason.append("Prediction based on: ");
        reason.append("Supplier delay rate: ").append(String.format("%.1f%%", supplier.getDelayRate()));
        if (daysUntilDelivery < 0) {
            reason.append("; Already past estimated delivery date");
        }
        if (shipment.getStatus() == Shipment.ShipmentStatus.DELAYED) {
            reason.append("; Currently marked as delayed");
        }

        boolean isDelayed = finalProbability > 0.5;

        // Check existing prediction
        Optional<DelayPrediction> existingPred = delayPredictionRepository.findByShipmentId(shipmentId);

        DelayPrediction prediction;
        if (existingPred.isPresent()) {
            prediction = existingPred.get();
            prediction.setPredictedDelayHours(predictedDelayHours);
            prediction.setConfidenceScore(BigDecimal.valueOf(Math.min(confidenceScore, 95.0))
                    .setScale(2, RoundingMode.HALF_UP));
            prediction.setPredictionReason(reason.toString());
            prediction.setIsDelayed(isDelayed);
        } else {
            prediction = new DelayPrediction();
            prediction.setShipment(shipment);
            prediction.setPredictedDelayHours(predictedDelayHours);
            prediction.setConfidenceScore(BigDecimal.valueOf(Math.min(confidenceScore, 95.0))
                    .setScale(2, RoundingMode.HALF_UP));
            prediction.setPredictionReason(reason.toString());
            prediction.setIsDelayed(isDelayed);
        }

        return delayPredictionRepository.save(prediction);
    }

    /**
     * Calculates risk and predictions for all shipments.
     */
    public void calculateAllRisks() {
        List<Shipment> shipments = shipmentRepository.findAll();
        for (Shipment shipment : shipments) {
            try {
                calculateRiskScore(shipment.getId());
                predictDelay(shipment.getId());
            } catch (Exception e) {
                // Log error but continue with other shipments
                System.err.println("Error calculating risk for shipment " + shipment.getId());
            }
        }
    }

    /**
     * Gets high-risk shipments.
     */
    public List<RiskScore> getHighRiskShipments() {
        return riskScoreRepository.findByRiskLevel(RiskScore.RiskLevel.HIGH);
    }

    /**
     * Gets critical-risk shipments.
     */
    public List<RiskScore> getCriticalRiskShipments() {
        return riskScoreRepository.findByRiskLevel(RiskScore.RiskLevel.CRITICAL);
    }

    /**
     * Gets shipments predicted to be delayed.
     */
    public List<DelayPrediction> getPredictedDelays() {
        return delayPredictionRepository.findByIsDelayedTrue();
    }

    public long countPredictedDelays() {
        return delayPredictionRepository.countByIsDelayedTrue();
    }

    /**
     * Gets risk score for a specific shipment.
     */
    public Optional<RiskScore> getRiskScoreByShipmentId(Long shipmentId) {
        return riskScoreRepository.findByShipmentId(shipmentId);
    }

    /**
     * Gets delay prediction for a specific shipment.
     */
    public Optional<DelayPrediction> getDelayPredictionByShipmentId(Long shipmentId) {
        return delayPredictionRepository.findByShipmentId(shipmentId);
    }

    /**
     * Gets risk statistics.
     */
    public long countByRiskLevel(RiskScore.RiskLevel level) {
        return riskScoreRepository.countByRiskLevel(level);
    }

    /**
     * Gets average risk score.
     */
    public Double getAverageRiskScore() {
        return riskScoreRepository.getAverageRiskScore();
    }

    /**
     * Gets risk level distribution.
     */
    public List<Object[]> getRiskLevelDistribution() {
        return riskScoreRepository.countByRiskLevelGrouped();
    }
}
