package com.nexusflow.repository;

import com.nexusflow.entity.RiskScore;
import com.nexusflow.entity.RiskScore.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RiskScore entity operations.
 * 
 * @author NexusFlow Team
 */
@Repository
public interface RiskScoreRepository extends JpaRepository<RiskScore, Long> {

    /**
     * Finds risk score by shipment ID.
     */
    Optional<RiskScore> findByShipmentId(Long shipmentId);

    /**
     * Finds all high-risk shipments.
     */
    List<RiskScore> findByRiskLevel(RiskLevel riskLevel);

    /**
     * Finds shipments with risk score above a threshold.
     */
    @Query("SELECT rs FROM RiskScore rs WHERE rs.riskScore >= :threshold")
    List<RiskScore> findByRiskScoreAbove(@Param("threshold") Double threshold);

    /**
     * Counts risk scores by level.
     */
    long countByRiskLevel(RiskLevel riskLevel);

    /**
     * Gets risk level distribution.
     */
    @Query("SELECT rs.riskLevel, COUNT(rs) FROM RiskScore rs GROUP BY rs.riskLevel")
    List<Object[]> countByRiskLevelGrouped();

    /**
     * Gets average risk score.
     */
    @Query("SELECT AVG(rs.riskScore) FROM RiskScore rs")
    Double getAverageRiskScore();

    /**
     * Finds top risky shipments.
     */
    @Query("SELECT rs FROM RiskScore rs ORDER BY rs.riskScore DESC LIMIT :limit")
    List<RiskScore> findTopRiskyShipments(@Param("limit") int limit);
}