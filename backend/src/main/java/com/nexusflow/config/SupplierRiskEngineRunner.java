package com.nexusflow.config;

import com.nexusflow.entity.Prediction;
import com.nexusflow.entity.Recommendation;
import com.nexusflow.entity.RiskEvent;
import com.nexusflow.entity.Supplier;
import com.nexusflow.repository.PredictionRepository;
import com.nexusflow.repository.RecommendationRepository;
import com.nexusflow.repository.RiskEventRepository;
import com.nexusflow.repository.SupplierRepository;
import com.nexusflow.service.SupplierRiskEngine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
public class SupplierRiskEngineRunner implements CommandLineRunner {

    private final SupplierRepository supplierRepository;
    private final SupplierRiskEngine supplierRiskEngine;
    private final RiskEventRepository riskEventRepository;
    private final PredictionRepository predictionRepository;
    private final RecommendationRepository recommendationRepository;

    public SupplierRiskEngineRunner(
            SupplierRepository supplierRepository,
            SupplierRiskEngine supplierRiskEngine,
            RiskEventRepository riskEventRepository,
            PredictionRepository predictionRepository,
            RecommendationRepository recommendationRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.supplierRiskEngine = supplierRiskEngine;
        this.riskEventRepository = riskEventRepository;
        this.predictionRepository = predictionRepository;
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1) Compute supplier risk scores & risk levels for all active suppliers
        List<Supplier> suppliers = supplierRepository.findByIsActiveTrue();
        for (Supplier supplier : suppliers) {
            var result = supplierRiskEngine.evaluate(supplier);

            supplier.setSupplierRiskScore(result.supplierRiskScore());
            supplier.setRiskLevel(result.riskLevel());

            supplierRepository.save(supplier);
        }

        // 2) Create intelligence artifacts for top-risk suppliers (simulation)
        //    Avoid spamming: only seed if there are already supplier failure events.
        if (riskEventRepository.count() > 0) return;

        List<Supplier> topSuppliers = suppliers.stream()

                .sorted(Comparator.comparing(Supplier::getSupplierRiskScore, Comparator.nullsLast(BigDecimal::compareTo)).reversed())
                .limit(3)
                .toList();

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < topSuppliers.size(); i++) {
            Supplier s = topSuppliers.get(i);

            RiskEvent event = new RiskEvent();
            event.setRiskType("SUPPLIER_FAILURE");
            event.setSeverity(toSeverity(s.getRiskLevel()));
            event.setAffectedEntityType("SUPPLIER");
            event.setAffectedEntityId(s.getId());
            event.setImpactAssessment("Supplier risk signals indicate elevated likelihood of disruptions impacting production continuity.");
            event.setRecommendedAction("Activate backup supplier and pre-book alternative logistics capacity.");
            event.setStatus(RiskEvent.Status.NEW);
            event.setCreatedAt(now.minusMinutes(25 - i * 5));
            event.setUpdatedAt(now.minusMinutes(25 - i * 5));
            riskEventRepository.save(event);

            Prediction pred = new Prediction();
            pred.setHorizonDays(14);
            pred.setRelatedSupplierId(s.getId());
            pred.setRelatedShipmentId(null);
            pred.setRelatedProductId(null);
            pred.setProbabilityPct(BigDecimal.valueOf(clampProbability(s.getSupplierRiskScore())));
            pred.setPredictionClass(Prediction.PredictionClass.SUPPLIER_FAILURE);
            pred.setConfidenceScore(BigDecimal.valueOf(70 + i * 4));
            pred.setExplanation("Supplier risk components (delivery, quality, financial, geopolitical, historical reliability) combine to a high disruption probability within 14 days.");
            pred.setPredictionReason("High supplier_risk_score + historic delay pattern");
            pred.setGeneratedAt(now.minusMinutes(18 - i * 4));
            predictionRepository.save(pred);

            Recommendation rec = new Recommendation();
            rec.setRecommendationType(Recommendation.RecommendationType.ACTIVATE_BACKUP_SUPPLIER);
            rec.setTargetEntityType("SUPPLIER");
            rec.setTargetEntityId(s.getId());
            rec.setRecommendationText("Activate backup supplier for critical components to reduce dependency risk.");
            rec.setParameters("{\"backupSupplierAllocationPercent\":30,\"reason\":\"supplier disruption mitigation\"}");
            rec.setStatus(Recommendation.Status.ACTIVE);
            rec.setCreatedAt(now.minusMinutes(12 - i * 3));
            rec.setUpdatedAt(now.minusMinutes(12 - i * 3));
            recommendationRepository.save(rec);
        }
    }

    private RiskEvent.Severity toSeverity(String riskLevel) {
        if (riskLevel == null) return RiskEvent.Severity.LOW;
        return switch (riskLevel) {
            case "CRITICAL" -> RiskEvent.Severity.CRITICAL;
            case "HIGH" -> RiskEvent.Severity.HIGH;
            case "MEDIUM" -> RiskEvent.Severity.MEDIUM;
            default -> RiskEvent.Severity.LOW;
        };
    }


    private double clampProbability(BigDecimal supplierRiskScore) {
        if (supplierRiskScore == null) return 30.0;
        // Convert score (roughly 0-500) to probability (0-99)
        double p = supplierRiskScore.doubleValue() / 500.0 * 100.0;
        if (p < 10) p = 10;
        if (p > 99) p = 99;
        return p;
    }
}

