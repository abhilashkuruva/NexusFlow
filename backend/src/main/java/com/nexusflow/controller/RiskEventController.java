package com.nexusflow.controller;

import com.nexusflow.entity.RiskEvent;
import com.nexusflow.entity.RiskEvent.Status;
import com.nexusflow.entity.RiskEvent.Severity;
import com.nexusflow.service.RiskEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/risk-events")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Risk Event API", description = "CRUD and query operations for risk events")
public class RiskEventController {

    @Autowired
    private RiskEventService riskEventService;

    @GetMapping
    @Operation(summary = "Get all risk events")
    public ResponseEntity<List<RiskEvent>> getAll() {
        return ResponseEntity.ok(riskEventService.getAllRiskEvents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get risk event by ID")
    public ResponseEntity<RiskEvent> getById(@PathVariable Long id) {
        RiskEvent event = riskEventService.getRiskEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get risk events by status")
    public ResponseEntity<List<RiskEvent>> getByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(riskEventService.getRiskEventsByStatus(status));
    }

    @GetMapping("/severity/{severity}")
    @Operation(summary = "Get risk events by severity")
    public ResponseEntity<List<RiskEvent>> getBySeverity(@PathVariable Severity severity) {
        return ResponseEntity.ok(riskEventService.getRiskEventsBySeverity(severity));
    }

    @GetMapping("/recent/{count}")
    @Operation(summary = "Get recent risk events")
    public ResponseEntity<List<RiskEvent>> getRecent(@PathVariable int count) {
        return ResponseEntity.ok(riskEventService.getRecentRiskEvents(count));
    }

    @PostMapping
    @Operation(summary = "Create a new risk event")
    public ResponseEntity<RiskEvent> create(@RequestBody RiskEvent riskEvent) {
        RiskEvent saved = riskEventService.saveRiskEvent(riskEvent);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a risk event by ID")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        riskEventService.deleteRiskEvent(id);
        return ResponseEntity.ok(Map.of("message", "Risk event deleted successfully"));
    }
}
