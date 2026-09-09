package com.nexusflow.service;

import com.nexusflow.entity.Prediction;
import com.nexusflow.entity.Recommendation;
import com.nexusflow.entity.RiskEvent;
import com.nexusflow.repository.PredictionRepository;
import com.nexusflow.repository.RecommendationRepository;
import com.nexusflow.repository.RiskEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class IntelligenceService {

    private final RiskEventRepository riskEventRepository;
    private final PredictionRepository predictionRepository;
    private final RecommendationRepository recommendationRepository;

    public IntelligenceService(
            RiskEventRepository riskEventRepository,
            PredictionRepository predictionRepository,
            RecommendationRepository recommendationRepository
    ) {
        this.riskEventRepository = riskEventRepository;
        this.predictionRepository = predictionRepository;
        this.recommendationRepository = recommendationRepository;
    }

    public List<RiskEvent> getRecentRiskEvents(int limit) {
        // Simple approach: fetch by NEW status (if seeded), otherwise fetch all and take top.
        List<RiskEvent> events = riskEventRepository.findTop10ByStatusOrderByCreatedAtDesc(RiskEvent.Status.NEW);
        if (events.size() >= limit) {
            return events.subList(0, limit);
        }
        // fallback: if not enough NEW events
        return events;
    }

    public List<Prediction> getTopPredictions(int limit) {
        return predictionRepository.findTop10ByPredictionClassOrderByGeneratedAtDesc(Prediction.PredictionClass.SHIPMENT_DELAY);
    }

    public List<Recommendation> getActiveRecommendations(int limit) {
        List<Recommendation> recs = recommendationRepository.findByStatus(Recommendation.Status.ACTIVE);
        if (recs.size() <= limit) {
            return recs;
        }
        return recs.subList(0, limit);
    }
}

