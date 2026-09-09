package com.nexusflow.repository;

import com.nexusflow.entity.Recommendation;
import com.nexusflow.entity.Recommendation.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByStatus(Status status);

    List<Recommendation> findTop10ByStatusOrderByCreatedAtDesc(Status status);
}

