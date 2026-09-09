package com.nexusflow.service;

import com.nexusflow.entity.Supplier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SupplierRiskEngine {

    public static final BigDecimal WEIGHT_DELIVERY = new BigDecimal("0.25");
    public static final BigDecimal WEIGHT_QUALITY = new BigDecimal("0.20");
    public static final BigDecimal WEIGHT_FINANCIAL = new BigDecimal("0.20");
    public static final BigDecimal WEIGHT_GEO = new BigDecimal("0.15");
    public static final BigDecimal WEIGHT_HISTORICAL = new BigDecimal("0.20");

    /**
     * Evaluates supplier risk score based on 5 components:
     * - Delivery Performance (25%)
     * - Quality Score (20%)
     * - Financial Stability (20%)
     * - Geopolitical Exposure (15%)
     * - Historical Reliability (20%)
     *
     * The final score is mathematically guaranteed to stay in the range [0.00, 100.00].
     */
    public SupplierRiskResult evaluate(Supplier supplier) {
        BigDecimal delivery = safe(supplier.getDeliveryPerformanceScore());
        BigDecimal quality = safe(supplier.getQualityScore());
        BigDecimal financial = safe(supplier.getFinancialStabilityScore());
        BigDecimal geo = safe(supplier.getGeopoliticalExposureScore());
        BigDecimal historical = safe(supplier.getHistoricalReliabilityScore());

        BigDecimal overall;

        // Check if values are pre-partitioned contributions (max 25, 20, 20, 15, 15)
        boolean isPrePartitioned = delivery.compareTo(new BigDecimal("25.0")) <= 0
                && quality.compareTo(new BigDecimal("20.0")) <= 0
                && financial.compareTo(new BigDecimal("20.0")) <= 0
                && geo.compareTo(new BigDecimal("15.0")) <= 0
                && historical.compareTo(new BigDecimal("15.0")) <= 0
                && (delivery.add(quality).add(financial).add(geo).add(historical)).compareTo(new BigDecimal("100.0")) <= 0
                && (delivery.compareTo(new BigDecimal("10.0")) > 0 || quality.compareTo(new BigDecimal("10.0")) > 0);

        if (isPrePartitioned) {
            overall = delivery.add(quality).add(financial).add(geo).add(historical);
        } else {
            // Standard normalized 0-100 scale with weights
            overall = delivery.multiply(WEIGHT_DELIVERY)
                    .add(quality.multiply(WEIGHT_QUALITY))
                    .add(financial.multiply(WEIGHT_FINANCIAL))
                    .add(geo.multiply(WEIGHT_GEO))
                    .add(historical.multiply(WEIGHT_HISTORICAL));
        }

        // Clamp strictly within [0.00, 100.00]
        overall = overall.max(BigDecimal.ZERO).min(new BigDecimal("100.00")).setScale(2, RoundingMode.HALF_UP);

        String riskLevel = supplier.classifySupplierRisk(overall);

        return new SupplierRiskResult(
                overall,
                riskLevel,
                delivery, quality, financial, geo, historical
        );
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public record SupplierRiskResult(
            BigDecimal supplierRiskScore,
            String riskLevel,
            BigDecimal deliveryPerformanceScore,
            BigDecimal qualityScore,
            BigDecimal financialStabilityScore,
            BigDecimal geopoliticalExposureScore,
            BigDecimal historicalReliabilityScore
    ) {}
}
