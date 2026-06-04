package com.nexusflow.controller;

import com.nexusflow.entity.RiskScore;
import com.nexusflow.entity.DelayPrediction;
import com.nexusflow.service.RiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for risk analysis and delay prediction operations.
 * 
 * Handles risk score calculations, delay predictions, and risk reports.
 * 
 * @author NexusFlow Team
 */
@RestController
@RequestMapping("/risk")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Risk Analysis", description = "Risk scoring and delay prediction APIs")
public class RiskController {

    @Autowired
    private RiskService riskService;

    /**
     * Calculates risk score for a specific shipment.
     */
    @PostMapping("/calculate/{shipmentId}")
    @Operation(summary = "Calculate Risk Score", 
               description = "Calculate risk score for a specific shipment")
    public ResponseEntity<Map<String, Object>> calculateRiskScore(
            @PathVariable Long shipmentId) {
        try {
            RiskScore riskScore = riskService.calculateRiskScore(shipmentId);
            return ResponseEntity.ok(convertRiskToMap(riskScore));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to calculate risk: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Predicts delay for a specific shipment.
     */
    @PostMapping("/predict/{shipmentId}")
    @Operation(summary = "Predict Delay", 
               description = "Predict delay for a specific shipment")
    public ResponseEntity<Map<String, Object>> predictDelay(
            @PathVariable Long shipmentId) {
        try {
            DelayPrediction prediction = riskService.predictDelay(shipmentId);
            return ResponseEntity.ok(convertPredictionToMap(prediction));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to predict delay: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Calculates risk and predictions for all shipments.
     */
    @PostMapping("/calculate-all")
    @Operation(summary = "Calculate All Risks", 
               description = "Calculate risk scores and predictions for all shipments")
    public ResponseEntity<Map<String, Object>> calculateAllRisks() {
        try {
            riskService.calculateAllRisks();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Risk calculations completed for all shipments");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to calculate risks: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Gets high-risk shipments.
     */
    @GetMapping("/high-risk")
    @Operation(summary = "Get High Risk Shipments", 
               description = "Retrieve all high-risk shipments")
    public ResponseEntity<List<Map<String, Object>>> getHighRiskShipments() {
        List<RiskScore> riskScores = riskService.getHighRiskShipments();
        return ResponseEntity.ok(riskScores.stream()
                .map(this::convertRiskToMap)
                .collect(Collectors.toList()));
    }

    /**
     * Gets critical-risk shipments.
     */
    @GetMapping("/critical-risk")
    @Operation(summary = "Get Critical Risk Shipments", 
               description = "Retrieve all critical-risk shipments")
    public ResponseEntity<List<Map<String, Object>>> getCriticalRiskShipments() {
        List<RiskScore> riskScores = riskService.getCriticalRiskShipments();
        return ResponseEntity.ok(riskScores.stream()
                .map(this::convertRiskToMap)
                .collect(Collectors.toList()));
    }

    /**
     * Gets shipments predicted to be delayed.
     */
    @GetMapping("/predicted-delays")
    @Operation(summary = "Get Predicted Delays", 
               description = "Retrieve shipments predicted to be delayed")
    public ResponseEntity<List<Map<String, Object>>> getPredictedDelays() {
        List<DelayPrediction> predictions = riskService.getPredictedDelays();
        return ResponseEntity.ok(predictions.stream()
                .map(this::convertPredictionToMap)
                .collect(Collectors.toList()));
    }

    /**
     * Gets risk score for a specific shipment.
     */
    @GetMapping("/score/{shipmentId}")
    @Operation(summary = "Get Risk Score by Shipment", 
               description = "Retrieve risk score for a specific shipment")
    public ResponseEntity<Map<String, Object>> getRiskScoreByShipment(
            @PathVariable Long shipmentId) {
        return riskService.getRiskScoreByShipmentId(shipmentId)
                .map(riskScore -> ResponseEntity.ok(convertRiskToMap(riskScore)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gets delay prediction for a specific shipment.
     */
    @GetMapping("/prediction/{shipmentId}")
    @Operation(summary = "Get Delay Prediction by Shipment", 
               description = "Retrieve delay prediction for a specific shipment")
    public ResponseEntity<Map<String, Object>> getDelayPredictionByShipment(
            @PathVariable Long shipmentId) {
        return riskService.getDelayPredictionByShipmentId(shipmentId)
                .map(prediction -> ResponseEntity.ok(convertPredictionToMap(prediction)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gets risk statistics.
     */
    @GetMapping("/stats/summary")
    @Operation(summary = "Get Risk Statistics", 
               description = "Get summary statistics for risk analysis")
    public ResponseEntity<Map<String, Object>> getRiskStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("lowRisk", riskService.countByRiskLevel(RiskScore.RiskLevel.LOW));
        stats.put("mediumRisk", riskService.countByRiskLevel(RiskScore.RiskLevel.MEDIUM));
        stats.put("highRisk", riskService.countByRiskLevel(RiskScore.RiskLevel.HIGH));
        stats.put("criticalRisk", riskService.countByRiskLevel(RiskScore.RiskLevel.CRITICAL));
        stats.put("averageRiskScore", riskService.getAverageRiskScore());
        stats.put("predictedDelays", riskService.getPredictedDelays().size());
        return ResponseEntity.ok(stats);
    }

    /**
     * Gets risk level distribution.
     */
    @GetMapping("/stats/distribution")
    @Operation(summary = "Get Risk Distribution", 
               description = "Get risk level distribution for chart visualization")
    public ResponseEntity<List<Map<String, Object>>> getRiskDistribution() {
        List<Object[]> distribution = riskService.getRiskLevelDistribution();
        List<Map<String, Object>> result = distribution.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("level", item[0]);
                    map.put("count", item[1]);
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // Helper methods
    private Map<String, Object> convertRiskToMap(RiskScore riskScore) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", riskScore.getId());
        map.put("shipmentId", riskScore.getShipment().getId());
        map.put("trackingNumber", riskScore.getShipment().getTrackingNumber());
        map.put("riskLevel", riskScore.getRiskLevel().name());
        map.put("riskScore", riskScore.getRiskScore());
        map.put("delayProbability", riskScore.getDelayProbability());
        map.put("factors", riskScore.getFactors());
        map.put("calculatedAt", riskScore.getCalculatedAt());
        return map;
    }

    private Map<String, Object> convertPredictionToMap(DelayPrediction prediction) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", prediction.getId());
        map.put("shipmentId", prediction.getShipment().getId());
        map.put("trackingNumber", prediction.getShipment().getTrackingNumber());
        map.put("predictedDelayHours", prediction.getPredictedDelayHours());
        map.put("confidenceScore", prediction.getConfidenceScore());
        map.put("predictionReason", prediction.getPredictionReason());
        map.put("isDelayed", prediction.getIsDelayed());
        map.put("predictedAt", prediction.getPredictedAt());
        return map;
    }
}