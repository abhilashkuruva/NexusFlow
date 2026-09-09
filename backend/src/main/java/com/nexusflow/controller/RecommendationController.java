package com.nexusflow.controller;

import com.nexusflow.entity.Recommendation;
import com.nexusflow.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<Recommendation>> getAll() {
        return ResponseEntity.ok(recommendationService.getAllRecommendations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recommendation> getById(@PathVariable Long id) {
        Recommendation rec = recommendationService.getRecommendationById(id);
        return (rec != null) ? ResponseEntity.ok(rec) : ResponseEntity.notFound().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Recommendation>> getByStatus(@PathVariable Recommendation.Status status) {
        return ResponseEntity.ok(recommendationService.getRecommendationsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Recommendation> create(@RequestBody Recommendation recommendation) {
        return ResponseEntity.ok(recommendationService.saveRecommendation(recommendation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recommendationService.deleteRecommendation(id);
        return ResponseEntity.noContent().build();
    }
}
