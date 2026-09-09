package com.nexusflow.controller;

import com.nexusflow.entity.Prediction;
import com.nexusflow.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/predictions")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Prediction API", description = "CRUD operations for prediction entities")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @GetMapping
    @Operation(summary = "Get all predictions")
    public ResponseEntity<List<Prediction>> getAllPredictions() {
        return ResponseEntity.ok(predictionService.getAllPredictions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prediction by ID")
    public ResponseEntity<Prediction> getPredictionById(@PathVariable Long id) {
        Prediction p = predictionService.getPredictionById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get predictions by supplier ID")
    public ResponseEntity<List<Prediction>> getBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(predictionService.getPredictionsBySupplierId(supplierId));
    }

    @GetMapping("/shipment/{shipmentId}")
    @Operation(summary = "Get predictions by shipment ID")
    public ResponseEntity<List<Prediction>> getByShipment(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(predictionService.getPredictionsByShipmentId(shipmentId));
    }

    @PostMapping
    @Operation(summary = "Create a new prediction")
    public ResponseEntity<Prediction> createPrediction(@RequestBody Prediction prediction) {
        Prediction saved = predictionService.savePrediction(prediction);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a prediction by ID")
    public ResponseEntity<Map<String, String>> deletePrediction(@PathVariable Long id) {
        predictionService.deletePrediction(id);
        return ResponseEntity.ok(Map.of("message", "Prediction deleted successfully"));
    }
}
