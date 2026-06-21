package com.nexusflow.service;

import com.nexusflow.entity.Shipment;
import com.nexusflow.entity.RiskScore;
import com.nexusflow.dto.AnalyticsDTO;
import com.nexusflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service class for Analytics and Dashboard statistics.
 * 
 * Aggregates data from various sources to provide comprehensive
 * analytics for the dashboard.
 * 
 * @author NexusFlow Team
 */
@Service
@Transactional
public class AnalyticsService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private RiskScoreRepository riskScoreRepository;

    @Autowired
    private DelayPredictionRepository delayPredictionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Gets comprehensive analytics data for the dashboard.
     */
    public AnalyticsDTO getDashboardAnalytics() {
        AnalyticsDTO analytics = new AnalyticsDTO();

        // Shipment statistics
        analytics.setTotalShipments(shipmentRepository.count());
        analytics.setPendingShipments(shipmentRepository.countByStatus(Shipment.ShipmentStatus.PENDING));
        analytics.setInTransitShipments(shipmentRepository.countByStatus(Shipment.ShipmentStatus.IN_TRANSIT));
        analytics.setDelayedShipments(shipmentRepository.countByStatus(Shipment.ShipmentStatus.DELAYED));
        analytics.setDeliveredShipments(shipmentRepository.countByStatus(Shipment.ShipmentStatus.DELIVERED));

        // Supplier statistics
        analytics.setTotalSuppliers(supplierRepository.countByIsActiveTrue());
        analytics.setAverageReliabilityScore(supplierRepository.getAverageReliabilityScore());

        // Risk statistics
        analytics.setHighRiskShipments(riskScoreRepository.countByRiskLevel(RiskScore.RiskLevel.HIGH));
        analytics.setCriticalRiskShipments(riskScoreRepository.countByRiskLevel(RiskScore.RiskLevel.CRITICAL));
        analytics.setAverageRiskScore(riskScoreRepository.getAverageRiskScore());

        // Delay prediction statistics
        analytics.setPredictedDelays(delayPredictionRepository.countByIsDelayedTrue());
        analytics.setAveragePredictedDelayHours(delayPredictionRepository.getAveragePredictedDelayHours());
        analytics.setAverageConfidenceScore(delayPredictionRepository.getAverageConfidenceScore());

        // Chart data
        analytics.setShipmentByStatus(getShipmentByStatusChart());
        analytics.setShipmentByMonth(getShipmentByMonthChart());
        analytics.setRiskByLevel(getRiskByLevelChart());
        analytics.setSuppliersByCountry(getSuppliersByCountryChart());

        return analytics;
    }

    /**
     * Gets shipment count grouped by status for charts.
     */
    private List<Map<String, Object>> getShipmentByStatusChart() {
        List<Object[]> results = shipmentRepository.countByStatusGrouped();
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("status", result[0]);
            item.put("count", result[1]);
            chartData.add(item);
        }

        return chartData;
    }

    /**
     * Gets monthly shipment count for charts.
     */
    private List<Map<String, Object>> getShipmentByMonthChart() {
        List<Object[]> results = shipmentRepository.getMonthlyShipmentCount();
        List<Map<String, Object>> chartData = new ArrayList<>();

        String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                               "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (Object[] result : results) {
            Map<String, Object> item = new HashMap<>();
            int month = ((Number) result[0]).intValue();
            item.put("month", monthNames[month]);
            item.put("count", result[1]);
            chartData.add(item);
        }

        return chartData;
    }

    /**
     * Gets risk level distribution for charts.
     */
    private List<Map<String, Object>> getRiskByLevelChart() {
        List<Object[]> results = riskScoreRepository.countByRiskLevelGrouped();
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("level", result[0]);
            item.put("count", result[1]);
            chartData.add(item);
        }

        return chartData;
    }

    /**
     * Gets suppliers grouped by country for charts.
     */
    private List<Map<String, Object>> getSuppliersByCountryChart() {
        List<Object[]> results = supplierRepository.countSuppliersByCountry();
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("country", result[0]);
            item.put("count", result[1]);
            chartData.add(item);
        }

        return chartData;
    }

    /**
     * Gets shipment summary statistics.
     */
    public Map<String, Object> getShipmentSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", shipmentRepository.count());
        summary.put("pending", shipmentRepository.countByStatus(Shipment.ShipmentStatus.PENDING));
        summary.put("inTransit", shipmentRepository.countByStatus(Shipment.ShipmentStatus.IN_TRANSIT));
        summary.put("delayed", shipmentRepository.countByStatus(Shipment.ShipmentStatus.DELAYED));
        summary.put("delivered", shipmentRepository.countByStatus(Shipment.ShipmentStatus.DELIVERED));
        return summary;
    }

    /**
     * Gets supplier summary statistics.
     */
    public Map<String, Object> getSupplierSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", supplierRepository.countByIsActiveTrue());
        summary.put("averageReliability", supplierRepository.getAverageReliabilityScore());
        return summary;
    }

    /**
     * Gets risk summary statistics.
     */
    public Map<String, Object> getRiskSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("highRisk", riskScoreRepository.countByRiskLevel(RiskScore.RiskLevel.HIGH));
        summary.put("criticalRisk", riskScoreRepository.countByRiskLevel(RiskScore.RiskLevel.CRITICAL));
        summary.put("averageScore", riskScoreRepository.getAverageRiskScore());
        return summary;
    }

    /**
     * Gets unread notification count for a user.
     */
    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Gets quick stats for dashboard cards.
     */
    public Map<String, Object> getQuickStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalShipments", shipmentRepository.count());
        stats.put("delayedShipments", shipmentRepository.countByStatus(Shipment.ShipmentStatus.DELAYED));
        stats.put("highRiskShipments", riskScoreRepository.countByRiskLevel(RiskScore.RiskLevel.HIGH) + 
                                       riskScoreRepository.countByRiskLevel(RiskScore.RiskLevel.CRITICAL));
        stats.put("unreadNotifications", notificationRepository.countByUserIdAndIsReadFalse(userId));
        stats.put("predictedDelays", delayPredictionRepository.countByIsDelayedTrue());
        return stats;
    }
}