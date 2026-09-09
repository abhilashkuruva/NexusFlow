package com.nexusflow.service;

import com.nexusflow.entity.Recommendation;
import com.nexusflow.entity.Recommendation.Status;
import java.util.List;

public interface RecommendationService {
    List<Recommendation> getAllRecommendations();
    Recommendation getRecommendationById(Long id);
    List<Recommendation> getRecommendationsByStatus(Status status);
    List<Recommendation> getRecentRecommendations(int count);
    Recommendation saveRecommendation(Recommendation recommendation);
    void deleteRecommendation(Long id);
}
