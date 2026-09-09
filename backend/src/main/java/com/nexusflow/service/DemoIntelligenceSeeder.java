package com.nexusflow.service;

import com.nexusflow.entity.Prediction;
import com.nexusflow.entity.Recommendation;
import com.nexusflow.entity.RiskEvent;
import com.nexusflow.entity.Alert;
import com.nexusflow.entity.Location;
import com.nexusflow.repository.PredictionRepository;
import com.nexusflow.repository.RecommendationRepository;
import com.nexusflow.repository.RiskEventRepository;
import com.nexusflow.repository.AlertRepository;
import com.nexusflow.repository.LocationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DemoIntelligenceSeeder implements CommandLineRunner {

    private final RiskEventRepository riskEventRepository;
    private final PredictionRepository predictionRepository;
    private final RecommendationRepository recommendationRepository;
    private final LocationRepository locationRepository;
    private final AlertRepository alertRepository;

    public DemoIntelligenceSeeder(
            RiskEventRepository riskEventRepository,
            PredictionRepository predictionRepository,
            RecommendationRepository recommendationRepository,
            LocationRepository locationRepository,
            AlertRepository alertRepository
    ) {
        this.riskEventRepository = riskEventRepository;
        this.predictionRepository = predictionRepository;
        this.recommendationRepository = recommendationRepository;
        this.locationRepository = locationRepository;
        this.alertRepository = alertRepository;
    }

    @Override
    public void run(String... args) {
        // Keep deterministic inserts only when tables are empty.
        if (riskEventRepository.count() == 0) {
            seed();
        }
    }

    private void seed() {
        LocalDateTime now = LocalDateTime.now();

        RiskEvent e1 = new RiskEvent();
        e1.setRiskType("SHIPMENT_DELAY");
        e1.setSeverity(RiskEvent.Severity.HIGH);
        e1.setAffectedEntityType("SHIPMENT");
        e1.setAffectedEntityId(2L);
        e1.setImpactAssessment("Port congestion and customs inspections are likely to extend the lead time.");
        e1.setRecommendedAction("Activate expedited clearance workflow and adjust arrival buffers.");
        e1.setStatus(RiskEvent.Status.NEW);
        e1.setCreatedAt(now.minusHours(2));
        e1.setUpdatedAt(now.minusHours(2));

        RiskEvent e2 = new RiskEvent();
        e2.setRiskType("INVENTORY_SHORTAGE");
        e2.setSeverity(RiskEvent.Severity.CRITICAL);
        e2.setAffectedEntityType("INVENTORY");
        e2.setAffectedEntityId(1L);
        e2.setImpactAssessment("Safety stock at warehouse A is expected to deplete within 21 days due to incoming delay.");
        e2.setRecommendedAction("Increase safety stock by 35% and reroute part of incoming shipments to alternate warehouse.");
        e2.setStatus(RiskEvent.Status.NEW);
        e2.setCreatedAt(now.minusHours(1));
        e2.setUpdatedAt(now.minusHours(1));

        riskEventRepository.saveAll(List.of(e1, e2));

        // Seed demo locations
        Location loc1 = new Location();
        loc1.setName("Port of Shanghai");
        loc1.setCountry("China");
        loc1.setCity("Shanghai");
        loc1.setAddress("Port Area");
        loc1.setLatitude(BigDecimal.valueOf(31.2304));
        loc1.setLongitude(BigDecimal.valueOf(121.4737));

        locationRepository.save(loc1);

        // Seed demo alerts linked to risk events
        Alert alert1 = new Alert();
        alert1.setRiskEvent(e1);
        alert1.setMessage("Potential customs delay at Shanghai port.");
        alert1.setSeverity("HIGH");
        alert1.setStatus("ACTIVE");
        alertRepository.save(alert1);

        Alert alert2 = new Alert();
        alert2.setRiskEvent(e2);
        alert2.setMessage("Inventory shortage critical for warehouse A.");
        alert2.setSeverity("CRITICAL");
        alert2.setStatus("ACTIVE");
        alertRepository.save(alert2);

        Prediction p1 = new Prediction();
        p1.setHorizonDays(14);
        p1.setRelatedShipmentId(2L);
        p1.setRelatedSupplierId(2L);
        p1.setRelatedProductId(null);
        p1.setProbabilityPct(BigDecimal.valueOf(87.00));
        p1.setPredictionClass(Prediction.PredictionClass.PRODUCTION_DELAY);
        p1.setConfidenceScore(BigDecimal.valueOf(84.00));
        p1.setExplanation("Supplier delay signals combined with customs variability drive a high likelihood of production disruption within 14 days.");
        p1.setPredictionReason("High supplier delay rate + elevated route complexity + near-term delivery window.");
        p1.setGeneratedAt(now.minusMinutes(30));

        Prediction p2 = new Prediction();
        p2.setHorizonDays(14);
        p2.setRelatedShipmentId(8L);
        p2.setRelatedSupplierId(4L);
        p2.setRelatedProductId(null);
        p2.setProbabilityPct(BigDecimal.valueOf(74.00));
        p2.setPredictionClass(Prediction.PredictionClass.SHIPMENT_DELAY);
        p2.setConfidenceScore(BigDecimal.valueOf(71.00));
        p2.setExplanation("Weather and route congestion increase the chance of delivery slip beyond expected arrival.");
        p2.setPredictionReason("Weather impact + current status IN_TRANSIT + logistics corridor instability.");
        p2.setGeneratedAt(now.minusMinutes(20));

        predictionRepository.saveAll(List.of(p1, p2));

        Recommendation r1 = new Recommendation();
        r1.setRecommendationType(Recommendation.RecommendationType.SWITCH_SUPPLIER_ALLOCATION);
        r1.setTargetEntityType("SUPPLIER");
        r1.setTargetEntityId(1L);
        r1.setRecommendationText("Switch 30% of production allocation to Supplier B to reduce dependency on the congested lane.");
        r1.setParameters("{\"allocationPercent\":30,\"reason\":\"mitigate delay\"}");
        r1.setStatus(Recommendation.Status.ACTIVE);
        r1.setCreatedAt(now.minusMinutes(15));
        r1.setUpdatedAt(now.minusMinutes(15));

        recommendationRepository.save(r1);
    }
}

