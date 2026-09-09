package com.nexusflow.service;

import com.nexusflow.entity.Prediction;
import java.util.List;

public interface PredictionService {
    List<Prediction> getAllPredictions();
    Prediction getPredictionById(Long id);
    List<Prediction> getPredictionsBySupplierId(Long supplierId);
    List<Prediction> getPredictionsByShipmentId(Long shipmentId);
    Prediction savePrediction(Prediction prediction);
    void deletePrediction(Long id);
}