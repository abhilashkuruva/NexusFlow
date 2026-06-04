package com.nexusflow.repository;

import com.nexusflow.entity.DelayPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DelayPrediction entity operations.
 * 
 * @author NexusFlow Team
 */
@Repository
public interface DelayPredictionRepository extends JpaRepository<DelayPrediction, Long> {

    /**
     * Finds delay prediction by shipment ID.
     */
    Optional<DelayPrediction> findByShipmentId(Long shipmentId);

    /**
     * Finds all predictions where delay is expected.
     */
    List<DelayPrediction> findByIsDelayedTrue();

    /**
     * Finds predictions with confidence above a threshold.
     */
    @Query("SELECT dp FROM DelayPrediction dp WHERE dp.confidenceScore >= :threshold")
    List<DelayPrediction> findByHighConfidence(@Param("threshold") Double threshold);

    /**
     * Counts predictions where delay is expected.
     */
    long countByIsDelayedTrue();

    /**
     * Gets average predicted delay hours.
     */
    @Query("SELECT AVG(dp.predictedDelayHours) FROM DelayPrediction dp WHERE dp.isDelayed = true")
    Double getAveragePredictedDelayHours();

    /**
     * Gets average confidence score.
     */
    @Query("SELECT AVG(dp.confidenceScore) FROM DelayPrediction dp")
    Double getAverageConfidenceScore();

    /**
     * Finds top shipments likely to be delayed.
     */
    @Query("SELECT dp FROM DelayPrediction dp WHERE dp.isDelayed = true " +
           "ORDER BY dp.confidenceScore DESC LIMIT :limit")
    List<DelayPrediction> findTopLikelyDelayed(@Param("limit") int limit);
}