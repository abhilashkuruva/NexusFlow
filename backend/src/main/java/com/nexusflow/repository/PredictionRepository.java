package com.nexusflow.repository;

import com.nexusflow.entity.Prediction;
import com.nexusflow.entity.Prediction.PredictionClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    List<Prediction> findByHorizonDays(Integer horizonDays);

    List<Prediction> findTop10ByPredictionClassOrderByGeneratedAtDesc(PredictionClass predictionClass);
    List<Prediction> findByRelatedSupplierId(Long supplierId);

    List<Prediction> findByRelatedShipmentId(Long shipmentId);
}



