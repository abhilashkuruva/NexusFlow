package com.nexusflow.serviceImpl;

import com.nexusflow.entity.Prediction;
import com.nexusflow.repository.PredictionRepository;
import com.nexusflow.service.PredictionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PredictionServiceImpl implements PredictionService {

    private final PredictionRepository predictionRepository;

    public PredictionServiceImpl(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    @Override
    public List<Prediction> getAllPredictions() {
        return predictionRepository.findAll();
    }

    @Override
    public Prediction getPredictionById(Long id) {
        Optional<Prediction> opt = predictionRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public List<Prediction> getPredictionsBySupplierId(Long supplierId) {
        return predictionRepository.findByRelatedSupplierId(supplierId);
    }

    @Override
    public List<Prediction> getPredictionsByShipmentId(Long shipmentId) {
        return predictionRepository.findByRelatedShipmentId(shipmentId);
    }

    @Override
    public Prediction savePrediction(Prediction prediction) {
        return predictionRepository.save(prediction);
    }

    @Override
    public void deletePrediction(Long id) {
        predictionRepository.deleteById(id);
    }
}
