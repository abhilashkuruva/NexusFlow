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

    /**
     * Calculates risk score for a shipment.
     * 
     * Risk calculation factors:
     * - Supplier reliability (weight: 30%)
     * - Shipment priority (weight: 20%)
     * - Days until delivery (weight: 15%)
     * - International shipment (weight: 15%)
     * - Cargo value (weight: 10%)
     * - Historical delay rate (weight: 10%)
     */
    public RiskScore calculateRiskScore(Long shipmentId) {
        Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
        
        if (shipmentOpt.isEmpty()) {
            throw new RuntimeException("Shipment not found");
        }

        Shipment shipment = shipmentOpt.get();
        Supplier supplier = shipment.getSupplier();

        // Factor 1: Supplier reliability (0-30 points)
        // Lower reliability = higher risk
        double reliabilityFactor = 0;
        if (supplier.getReliabilityScore() != null) {
            double reliability = supplier.getReliabilityScore().doubleValue();
            reliabilityFactor = (5.0 - Math.min(reliability, 5.0)) / 5.0 * 30.0;
        }

        // Factor 2: Priority (0-20 points)
        // Higher priority = higher risk
        int priorityFactor = 0;
        switch (shipment.getPriority()) {
            case URGENT: priorityFactor = 20; break;
            case HIGH: priorityFactor = 15; break;
            case MEDIUM: priorityFactor = 10; break;
            case LOW: priorityFactor = 5; break;
        }

        // Factor 3: Days until delivery (0-15 points)
        // Fewer days = higher risk
        long daysUntilDelivery = ChronoUnit.DAYS.between(LocalDate.now(), 
                                                          shipment.getEstimatedDeliveryDate());
        int timeFactor = 0;
        if (daysUntilDelivery < 3) {
            timeFactor = 15;
        } else if (daysUntilDelivery < 7) {
            timeFactor = 10;
        } else if (daysUntilDelivery < 14) {
            timeFactor = 5;
        }

        // Factor 4: International shipment (0-15 points)
        int internationalFactor = shipment.getOriginCountry().equals(shipment.getDestinationCountry()) 
                                ? 0 : 15;

        // Factor 5: Cargo value (0-10 points)
        int valueFactor = 0;
        if (shipment.getValueUsd() != null) {
            double value = shipment.getValueUsd().doubleValue();
            if (value > 100000) {
                valueFactor = 10;
            } else if (value > 50000) {
                valueFactor = 7;
            } else if (value > 25000) {
                valueFactor = 4;
            } else {
                valueFactor = 2;
            }
        }

        // Factor 6: Historical delay rate (0-10 points)
        double delayRateFactor = supplier.getDelayRate() / 100.0 * 10.0;

        // Calculate total risk score
        double totalRisk = reliabilityFactor + priorityFactor + timeFactor + 
                          internationalFactor + valueFactor + delayRateFactor;

        // Determine risk level
        RiskScore.RiskLevel riskLevel;
        if (totalRisk <= 25) {
            riskLevel = RiskScore.RiskLevel.LOW;
        } else if (totalRisk <= 50) {
            riskLevel = RiskScore.RiskLevel.MEDIUM;
        } else if (totalRisk <= 75) {
            riskLevel = RiskScore.RiskLevel.HIGH;
        } else {
            riskLevel = RiskScore.RiskLevel.CRITICAL;
        }

        // Calculate delay probability (similar to risk but normalized to 0-100)
        double delayProbability = totalRisk;

        // Build factors description
        StringBuilder factors = new StringBuilder();
        factors.append("Risk factors: ");
        if (reliabilityFactor > 15) factors.append("Low supplier reliability; ");
        if (priorityFactor >= 15) factors.append("High priority shipment; ");
        if (timeFactor >= 10) factors.append("Tight delivery timeline; ");
        if (internationalFactor > 0) factors.append("International shipment; ");
        if (valueFactor >= 7) factors.append("High value cargo; ");
        if (delayRateFactor > 5) factors.append("Supplier has high delay rate; ");

        // Check existing risk score
        Optional<RiskScore> existingRisk = riskScoreRepository.findByShipmentId(shipmentId);

        RiskScore riskScore;
        if (existingRisk.isPresent()) {
            riskScore = existingRisk.get();
            riskScore.setRiskLevel(riskLevel);
            riskScore.setRiskScore(BigDecimal.valueOf(totalRisk).setScale(2, RoundingMode.HALF_UP));
            riskScore.setDelayProbability(BigDecimal.valueOf(delayProbability).setScale(2, RoundingMode.HALF_UP));
            riskScore.setFactors(factors.toString());
        } else {
            riskScore = new RiskScore();
            riskScore.setShipment(shipment);
            riskScore.setRiskLevel(riskLevel);
            riskScore.setRiskScore(BigDecimal.valueOf(totalRisk).setScale(2, RoundingMode.HALF_UP));
            riskScore.setDelayProbability(BigDecimal.valueOf(delayProbability).setScale(2, RoundingMode.HALF_UP));
            riskScore.setFactors(factors.toString());
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