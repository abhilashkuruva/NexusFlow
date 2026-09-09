package com.nexusflow.controller;

import com.nexusflow.dto.AnalyticsDTO;
import com.nexusflow.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for analytics and dashboard operations.
 * 
 * Provides aggregated data for dashboard visualization and reporting.
 * 
 * @author NexusFlow Team
 */
@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Analytics", description = "Dashboard analytics and statistics APIs")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Gets comprehensive dashboard analytics.
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get Dashboard Analytics", 
               description = "Retrieve comprehensive analytics data for the dashboard")
    public ResponseEntity<AnalyticsDTO> getDashboardAnalytics() {
        AnalyticsDTO analytics = analyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Gets quick statistics for dashboard cards.
     */
    @GetMapping("/quick-stats")
    @Operation(summary = "Get Quick Stats", 
               description = "Retrieve quick statistics for dashboard cards")
    public ResponseEntity<Map<String, Object>> getQuickStats(
            @RequestParam(required = false) Long userId) {
        Long uid = userId != null ? userId : 1L; // Default to first user
        return ResponseEntity.ok(analyticsService.getQuickStats(uid));
    }

    /**
     * Gets shipment summary statistics.
     */
    @GetMapping("/shipments/summary")
    @Operation(summary = "Get Shipment Summary", 
               description = "Retrieve summary statistics for shipments")
    public ResponseEntity<Map<String, Object>> getShipmentSummary() {
        return ResponseEntity.ok(analyticsService.getShipmentSummary());
    }

    /**
     * Gets supplier summary statistics.
     */
    @GetMapping("/suppliers/summary")
    @Operation(summary = "Get Supplier Summary", 
               description = "Retrieve summary statistics for suppliers")
    public ResponseEntity<Map<String, Object>> getSupplierSummary() {
        return ResponseEntity.ok(analyticsService.getSupplierSummary());
    }

    /**
     * Gets risk summary statistics.
     */
    @GetMapping("/risk/summary")
    @Operation(summary = "Get Risk Summary", 
               description = "Retrieve summary statistics for risk analysis")
    public ResponseEntity<Map<String, Object>> getRiskSummary() {
        return ResponseEntity.ok(analyticsService.getRiskSummary());
    }

    /**
     * Gets unread notification count.
     */
    @GetMapping("/notifications/unread")
    @Operation(summary = "Get Unread Notification Count", 
               description = "Retrieve count of unread notifications for a user")
    public ResponseEntity<Map<String, Object>> getUnreadNotificationCount(
            @RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", analyticsService.getUnreadNotificationCount(userId));
        return ResponseEntity.ok(response);
    }

    /**
     * Gets chart data for shipment status distribution.
     */
    @GetMapping("/charts/shipment-status")
    @Operation(summary = "Get Shipment Status Chart Data", 
               description = "Retrieve chart data for shipment status distribution")
    public ResponseEntity<Object> getShipmentStatusChart() {
        AnalyticsDTO analytics = analyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics.getShipmentByStatus());
    }

    /**
     * Gets chart data for monthly shipments.
     */
    @GetMapping("/charts/monthly-shipments")
    @Operation(summary = "Get Monthly Shipments Chart Data", 
               description = "Retrieve chart data for monthly shipment trends")
    public ResponseEntity<Object> getMonthlyShipmentsChart() {
        AnalyticsDTO analytics = analyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics.getShipmentByMonth());
    }

    /**
     * Gets chart data for risk level distribution.
     */
    @GetMapping("/charts/risk-distribution")
    @Operation(summary = "Get Risk Distribution Chart Data", 
               description = "Retrieve chart data for risk level distribution")
    public ResponseEntity<Object> getRiskDistributionChart() {
        AnalyticsDTO analytics = analyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics.getRiskByLevel());
    }

    /**
     * Gets chart data for suppliers by country.
     */
    @GetMapping("/charts/suppliers-by-country")
    @Operation(summary = "Get Suppliers by Country Chart Data", 
               description = "Retrieve chart data for supplier distribution by country")
    public ResponseEntity<Object> getSuppliersByCountryChart() {
        AnalyticsDTO analytics = analyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics.getSuppliersByCountry());
    }
}