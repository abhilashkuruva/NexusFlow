package com.nexusflow.serviceImpl;

import com.nexusflow.entity.Recommendation;
import com.nexusflow.entity.Recommendation.Status;
import com.nexusflow.repository.RecommendationRepository;
import com.nexusflow.service.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public List<Recommendation> getAllRecommendations() {
        return recommendationRepository.findAll();
    }

    @Override
    public Recommendation getRecommendationById(Long id) {
        Optional<Recommendation> opt = recommendationRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public List<Recommendation> getRecommendationsByStatus(Status status) {
        return recommendationRepository.findByStatus(status);
    }

    @Override
    public List<Recommendation> getRecentRecommendations(int count) {
        // Using the existing method to get top 10; ignore count for now.
        return recommendationRepository.findTop10ByStatusOrderByCreatedAtDesc(Status.ACTIVE);
    }

    @Override
    public Recommendation saveRecommendation(Recommendation recommendation) {
        return recommendationRepository.save(recommendation);
    }

    @Override
    public void deleteRecommendation(Long id) {
        recommendationRepository.deleteById(id);
    }
}
