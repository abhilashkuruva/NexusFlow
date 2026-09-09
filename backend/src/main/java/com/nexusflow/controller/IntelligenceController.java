package com.nexusflow.controller;

import com.nexusflow.entity.Prediction;
import com.nexusflow.entity.Recommendation;
import com.nexusflow.entity.RiskEvent;
import com.nexusflow.service.IntelligenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/intelligence")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Intelligence", description = "AI risk intelligence endpoints")
public class IntelligenceController {

    private final IntelligenceService intelligenceService;

    public IntelligenceController(IntelligenceService intelligenceService) {
        this.intelligenceService = intelligenceService;
    }

    @GetMapping("/events/recent")
    @Operation(summary = "Get recent risk events")
    public ResponseEntity<List<Map<String, Object>>> recentRiskEvents(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RiskEvent> events = intelligenceService.getRecentRiskEvents(limit);
        return ResponseEntity.ok(events.stream().map(this::toMap).toList());
    }

    @GetMapping("/recommendations/active")
    @Operation(summary = "Get active recommendations")
    public ResponseEntity<List<Map<String, Object>>> activeRecommendations(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<Recommendation> recs = intelligenceService.getActiveRecommendations(limit);
        return ResponseEntity.ok(recs.stream().map(this::toMap).toList());
    }

    @GetMapping("/predictions/top")
    @Operation(summary = "Get top predictions")
    public ResponseEntity<List<Map<String, Object>>> topPredictions(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<Prediction> preds = intelligenceService.getTopPredictions(limit);
        return ResponseEntity.ok(preds.stream().map(this::toMap).toList());
    }

    private Map<String, Object> toMap(RiskEvent e) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", e.getId());
        m.put("riskType", e.getRiskType());
        m.put("severity", e.getSeverity() != null ? e.getSeverity().name() : null);
        m.put("affectedEntityType", e.getAffectedEntityType());
        m.put("affectedEntityId", e.getAffectedEntityId());
        m.put("impactAssessment", e.getImpactAssessment());
        m.put("recommendedAction", e.getRecommendedAction());
        m.put("status", e.getStatus() != null ? e.getStatus().name() : null);
        m.put("createdAt", e.getCreatedAt());
        return m;
    }

    private Map<String, Object> toMap(Recommendation r) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", r.getId());
        m.put("recommendationType", r.getRecommendationType() != null ? r.getRecommendationType().name() : null);
        m.put("targetEntityType", r.getTargetEntityType());
        m.put("targetEntityId", r.getTargetEntityId());
        m.put("recommendationText", r.getRecommendationText());
        m.put("parameters", r.getParameters());
        m.put("status", r.getStatus() != null ? r.getStatus().name() : null);
        m.put("createdAt", r.getCreatedAt());
        return m;
    }

    private Map<String, Object> toMap(Prediction p) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", p.getId());
        m.put("horizonDays", p.getHorizonDays());
        m.put("relatedSupplierId", p.getRelatedSupplierId());
        m.put("relatedShipmentId", p.getRelatedShipmentId());
        m.put("relatedProductId", p.getRelatedProductId());
        m.put("probabilityPct", p.getProbabilityPct());
        m.put("predictionClass", p.getPredictionClass() != null ? p.getPredictionClass().name() : null);
        m.put("confidenceScore", p.getConfidenceScore());
        m.put("explanation", p.getExplanation());
        m.put("predictionReason", p.getPredictionReason());
        m.put("generatedAt", p.getGeneratedAt());
        return m;
    }
}

