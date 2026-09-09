package com.nexusflow.config;

import com.nexusflow.entity.DelayPrediction;
import com.nexusflow.entity.Inventory;
import com.nexusflow.entity.Notification;
import com.nexusflow.entity.Product;
import com.nexusflow.entity.RiskScore;
import com.nexusflow.entity.Shipment;
import com.nexusflow.entity.Supplier;
import com.nexusflow.entity.User;
import com.nexusflow.entity.Warehouse;
import com.nexusflow.repository.DelayPredictionRepository;
import com.nexusflow.repository.InventoryRepository;
import com.nexusflow.repository.NotificationRepository;
import com.nexusflow.repository.ProductRepository;
import com.nexusflow.repository.RiskScoreRepository;
import com.nexusflow.repository.ShipmentRepository;
import com.nexusflow.repository.SupplierRepository;
import com.nexusflow.repository.UserRepository;
import com.nexusflow.repository.WarehouseRepository;
import com.nexusflow.service.DelayPredictionModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class DemoDataSeeder {

    private static final int TARGET_NEW_SHIPMENTS = 100;
    private static final String TRACKING_PREFIX = "NXF-2026-";

    private final SupplierRepository supplierRepository;
    private final ShipmentRepository shipmentRepository;
    private final RiskScoreRepository riskScoreRepository;
    private final DelayPredictionRepository delayPredictionRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    public DemoDataSeeder(
            SupplierRepository supplierRepository,
            ShipmentRepository shipmentRepository,
            RiskScoreRepository riskScoreRepository,
            DelayPredictionRepository delayPredictionRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            WarehouseRepository warehouseRepository,
            InventoryRepository inventoryRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.shipmentRepository = shipmentRepository;
        this.riskScoreRepository = riskScoreRepository;
        this.delayPredictionRepository = delayPredictionRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void seedDemoData() {
        ensureDemoSuppliers();
        ensureDemoProducts();
        ensureDemoWarehouses();
        ensureDemoInventory();
        ensureDemoShipments();
    }

    private void ensureDemoSuppliers() {
        Set<String> existingEmails = new HashSet<>();
        for (Supplier supplier : supplierRepository.findAll()) {
            if (supplier.getEmail() != null) {
                existingEmails.add(supplier.getEmail().toLowerCase());
            }
        }

        LocalDateTime now = LocalDateTime.now();
        for (SupplierSeed seed : SUPPLIER_SEEDS) {
            if (existingEmails.contains(seed.email().toLowerCase())) {
                continue;
            }

            Supplier supplier = new Supplier();
            supplier.setName(seed.name());
            supplier.setCompanyName(seed.companyName());
            supplier.setContactPerson(seed.contactPerson());
            supplier.setEmail(seed.email());
            supplier.setPhone(seed.phone());
            supplier.setAddress(seed.address());
            supplier.setCountry(seed.country());
            supplier.setLocation(seed.location());
            supplier.setIndustry(seed.industry());
            supplier.setReliabilityScore(BigDecimal.valueOf(seed.reliabilityScore()).setScale(2, RoundingMode.HALF_UP));
            supplier.setTotalShipments(seed.totalShipments());
            supplier.setDelayedShipments(seed.delayedShipments());
            supplier.setRiskLevel(seed.riskLevel());
            supplier.setCreatedAt(now);
            supplier.setIsActive(true);

            supplierRepository.save(supplier);
        }
    }

    private void ensureDemoShipments() {
        List<Supplier> suppliers = new ArrayList<>(supplierRepository.findAll());
        suppliers.sort(Comparator.comparing(Supplier::getId));

        if (suppliers.isEmpty()) {
            return;
        }

        Map<Long, SupplierMetrics> supplierMetrics = new HashMap<>();
        for (Supplier supplier : suppliers) {
            supplierMetrics.put(
                    supplier.getId(),
                    new SupplierMetrics(
                            supplier.getTotalShipments() != null ? supplier.getTotalShipments() : 0,
                            supplier.getDelayedShipments() != null ? supplier.getDelayedShipments() : 0
                    )
            );
        }

        User admin = userRepository.findByEmail("admin@nexusflow.com").orElse(null);
        User manager = userRepository.findByEmail("manager@nexusflow.com").orElse(admin);
        User analyst = userRepository.findByEmail("analyst@nexusflow.com").orElse(admin);
        List<User> notificationUsers = new ArrayList<>();
        if (admin != null) {
            notificationUsers.add(admin);
        }
        if (manager != null && manager != admin) {
            notificationUsers.add(manager);
        }
        if (analyst != null && analyst != admin && analyst != manager) {
            notificationUsers.add(analyst);
        }

        LocalDate baseDate = LocalDate.now().minusDays(120);
        for (int i = 1; i <= TARGET_NEW_SHIPMENTS; i++) {
            String trackingNumber = TRACKING_PREFIX + String.format("%04d", i);
            if (shipmentRepository.findByTrackingNumber(trackingNumber) != null) {
                continue;
            }

            Supplier supplier = suppliers.get((i - 1) % suppliers.size());
            RouteSeed route = ROUTES.get((i - 1) % ROUTES.size());
            String cargoType = CARGO_TYPES[(i - 1) % CARGO_TYPES.length];

            SupplierMetrics metrics = supplierMetrics.get(supplier.getId());
            double supplierDelayRate = metrics.totalShipments() > 0
                    ? (double) metrics.delayedShipments() / metrics.totalShipments() * 100.0
                    : 0.0;
            double reliability = supplier.getReliabilityScore() != null
                    ? supplier.getReliabilityScore().doubleValue()
                    : 3.5;

            Shipment shipment = new Shipment();
            shipment.setTrackingNumber(trackingNumber);
            shipment.setSupplier(supplier);
            shipment.setOriginCity(route.originCity());
            shipment.setOriginCountry(route.originCountry());
            shipment.setDestinationCity(route.destinationCity());
            shipment.setDestinationCountry(route.destinationCountry());
            shipment.setShipmentDate(baseDate.plusDays(i));
            shipment.setEstimatedDeliveryDate(baseDate.plusDays(i).plusDays(route.transitDays()));
            shipment.setCargoType(cargoType);
            shipment.setWeightKg(BigDecimal.valueOf(route.baseWeightKg() + (i % 9) * 37.5).setScale(2, RoundingMode.HALF_UP));
            shipment.setValueUsd(BigDecimal.valueOf(route.baseValueUsd() + (i % 11) * 4200.0).setScale(2, RoundingMode.HALF_UP));
            shipment.setPriority(route.priority());
            shipment.setCurrentLocation(route.currentLocation());
            shipment.setWeatherImpactScore(BigDecimal.valueOf(route.weatherImpact()).setScale(2, RoundingMode.HALF_UP));
            shipment.setRouteComplexityScore(BigDecimal.valueOf(route.routeComplexity()).setScale(2, RoundingMode.HALF_UP));
            shipment.setInventoryRiskScore(BigDecimal.valueOf(route.inventoryRisk()).setScale(2, RoundingMode.HALF_UP));
            shipment.setNotes("Seeded demo shipment for " + route.originCity() + " to " + route.destinationCity());
            shipment.setStatus(determineShipmentStatus(i, route));
            if (shipment.getStatus() == Shipment.ShipmentStatus.DELIVERED) {
                shipment.setActualDeliveryDate(shipment.getEstimatedDeliveryDate().minusDays(1));
            } else if (shipment.getStatus() == Shipment.ShipmentStatus.CANCELLED) {
                shipment.setActualDeliveryDate(null);
            } else if (shipment.getStatus() == Shipment.ShipmentStatus.IN_TRANSIT && i % 8 == 0) {
                shipment.setActualDeliveryDate(null);
            }
            shipment.setCreatedAt(LocalDateTime.now().minusDays(30 - (i % 30)));
            shipment.setUpdatedAt(LocalDateTime.now());

            Shipment savedShipment = shipmentRepository.save(shipment);

            double delayProbability = DelayPredictionModel.predictProbability(savedShipment, supplier);
            String riskCategory = DelayPredictionModel.determineRiskCategory(delayProbability);
            BigDecimal supplierRisk = BigDecimal.valueOf(Math.min(100.0, (100.0 - reliability * 18.0) + supplierDelayRate * 0.45))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal weatherRisk = BigDecimal.valueOf(route.weatherImpact() * 1.4).setScale(2, RoundingMode.HALF_UP);
            BigDecimal routeRisk = BigDecimal.valueOf(route.routeComplexity() * 1.5).setScale(2, RoundingMode.HALF_UP);
            BigDecimal inventoryRisk = BigDecimal.valueOf(route.inventoryRisk() * 1.7).setScale(2, RoundingMode.HALF_UP);
            BigDecimal overallScore = BigDecimal.valueOf(Math.min(
                    100.0,
                    supplierRisk.doubleValue() * 0.30
                            + weatherRisk.doubleValue() * 0.25
                            + routeRisk.doubleValue() * 0.25
                            + inventoryRisk.doubleValue() * 0.20
            )).setScale(2, RoundingMode.HALF_UP);

            RiskScore riskScore = riskScoreRepository.findByShipmentId(savedShipment.getId()).orElseGet(RiskScore::new);
            riskScore.setShipment(savedShipment);
            riskScore.setSupplierRisk(supplierRisk);
            riskScore.setDelayProbability(BigDecimal.valueOf(delayProbability).setScale(2, RoundingMode.HALF_UP));
            riskScore.setWeatherRisk(weatherRisk);
            riskScore.setRouteRisk(routeRisk);
            riskScore.setInventoryRisk(inventoryRisk);
            riskScore.setOverallScore(overallScore);
            riskScore.setRiskLevel(RiskScore.RiskLevel.valueOf(riskCategory));
            riskScore.setGeneratedTime(LocalDateTime.now());
            riskScore.setFactors(route.factorSummary());
            riskScoreRepository.save(riskScore);

            DelayPrediction delayPrediction = delayPredictionRepository.findByShipmentId(savedShipment.getId()).orElseGet(DelayPrediction::new);
            delayPrediction.setShipment(savedShipment);
            delayPrediction.setPredictedDelayHours(calculatePredictedDelayHours(savedShipment, delayProbability));
            delayPrediction.setConfidenceScore(BigDecimal.valueOf(calculateConfidenceScore(supplier, delayProbability))
                    .setScale(2, RoundingMode.HALF_UP));
            delayPrediction.setPredictionReason(route.reason());
            delayPrediction.setIsDelayed(delayProbability >= 45.0);
            delayPrediction.setPredictedAt(LocalDateTime.now());
            delayPrediction.setUpdatedAt(LocalDateTime.now());
            delayPredictionRepository.save(delayPrediction);

            SupplierMetrics updatedMetrics = metrics.increment(savedShipment.getStatus());
            supplierMetrics.put(supplier.getId(), updatedMetrics);
            supplier.setTotalShipments(updatedMetrics.totalShipments());
            supplier.setDelayedShipments(updatedMetrics.delayedShipments());
            supplier.setRiskLevel(riskCategory);
            supplierRepository.save(supplier);

            maybeCreateNotification(notificationUsers, savedShipment, riskCategory, route.reason());
        }
    }

    private Shipment.ShipmentStatus determineShipmentStatus(int index, RouteSeed route) {
        int mod = index % 10;
        return switch (mod) {
            case 1 -> Shipment.ShipmentStatus.CREATED;
            case 2, 8 -> Shipment.ShipmentStatus.IN_TRANSIT;
            case 3, 7 -> Shipment.ShipmentStatus.DELAYED;
            case 4 -> Shipment.ShipmentStatus.PENDING;
            case 5 -> Shipment.ShipmentStatus.IN_TRANSIT;
            case 6 -> Shipment.ShipmentStatus.DELIVERED;
            case 9 -> Shipment.ShipmentStatus.CANCELLED;
            default -> route.preferredStatus();
        };
    }

    private int calculatePredictedDelayHours(Shipment shipment, double delayProbability) {
        if (delayProbability < 35.0) {
            return 0;
        }

        int baseHours = (int) Math.round((delayProbability - 30.0) * 0.9);
        if (shipment.getPriority() == Shipment.Priority.URGENT) {
            baseHours += 8;
        }
        return Math.max(baseHours, 6);
    }

    private double calculateConfidenceScore(Supplier supplier, double delayProbability) {
        double reliability = supplier.getReliabilityScore() != null ? supplier.getReliabilityScore().doubleValue() : 3.5;
        double confidence = 62.0 + (delayProbability / 4.0) + (reliability * 3.0);
        return Math.min(confidence, 96.0);
    }

    private void maybeCreateNotification(List<User> notificationUsers, Shipment shipment, String riskCategory, String reason) {
        if (notificationRepository.findByRelatedShipmentIdOrderByCreatedAtDesc(shipment.getId()).size() > 0) {
            return;
        }

        if (!"HIGH".equals(riskCategory) && !"CRITICAL".equals(riskCategory)) {
            return;
        }

        User recipient = notificationUsers.stream().filter(user -> user != null).findFirst().orElse(null);
        if (recipient == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setTitle("Shipment risk alert");
        notification.setMessage("Shipment " + shipment.getTrackingNumber() + " is " + riskCategory + ". " + reason);
        notification.setType("CRITICAL".equals(riskCategory) ? Notification.NotificationType.ALERT : Notification.NotificationType.WARNING);
        notification.setIsRead(false);
        notification.setRelatedShipmentId(shipment.getId());
        notification.setRelatedSupplierId(shipment.getSupplier().getId());
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private record SupplierSeed(
            String name,
            String companyName,
            String contactPerson,
            String email,
            String phone,
            String address,
            String country,
            String location,
            String industry,
            double reliabilityScore,
            int totalShipments,
            int delayedShipments,
            String riskLevel
    ) {}

    private record RouteSeed(
            String originCity,
            String originCountry,
            String destinationCity,
            String destinationCountry,
            String currentLocation,
            Shipment.Priority priority,
            Shipment.ShipmentStatus preferredStatus,
            int transitDays,
            double weatherImpact,
            double routeComplexity,
            double inventoryRisk,
            double baseWeightKg,
            double baseValueUsd,
            String reason,
            String factorSummary
    ) {}

    private record SupplierMetrics(int totalShipments, int delayedShipments) {
        SupplierMetrics increment(Shipment.ShipmentStatus status) {
            int total = totalShipments + 1;
            int delayed = delayedShipments + (status == Shipment.ShipmentStatus.DELAYED ? 1 : 0);
            return new SupplierMetrics(total, delayed);
        }
    }

    private static final List<SupplierSeed> SUPPLIER_SEEDS = List.of(
            new SupplierSeed("Global Electronics Manufacturing", "Global Electronics Manufacturing", "Mia Chen", "ops@globalelectronics.com", "+86-21-5501-1101", "88 Pudong Avenue", "China", "Shanghai", "Electronics", 4.7, 180, 12, "LOW"),
            new SupplierSeed("Asia Components Pvt Ltd", "Asia Components Pvt Ltd", "Arjun Rao", "contact@asiacomponents.in", "+91-22-4100-2001", "14 MIDC Industrial Park", "India", "Mumbai", "Components", 4.3, 160, 18, "MEDIUM"),
            new SupplierSeed("Euro Parts GmbH", "Euro Parts GmbH", "Lukas Weber", "service@europarts.de", "+49-30-8844-3201", "19 Hafenstraße", "Germany", "Hamburg", "Automotive", 4.8, 140, 8, "LOW"),
            new SupplierSeed("Pacific Logistics Group", "Pacific Logistics Group", "Sofia Ramirez", "alerts@pacificlogistics.com", "+1-415-555-1204", "300 Harbor Way", "USA", "San Francisco", "Logistics", 4.2, 210, 28, "MEDIUM"),
            new SupplierSeed("NorthStar Industrial Supply", "NorthStar Industrial Supply", "Ethan Moore", "dispatch@northstarind.com", "+1-312-555-0189", "640 Lakeview Drive", "USA", "Chicago", "Industrial", 4.5, 190, 14, "LOW"),
            new SupplierSeed("Atlas Semiconductor Co", "Atlas Semiconductor Co", "Yuki Tanaka", "sales@atlassemi.jp", "+81-3-4580-7712", "5 Kanda Square", "Japan", "Tokyo", "Semiconductors", 4.6, 175, 10, "LOW"),
            new SupplierSeed("Meridian Auto Parts", "Meridian Auto Parts", "Noah Fischer", "orders@meridianautoparts.eu", "+33-1-4422-3901", "41 Rue de la Gare", "France", "Lyon", "Automotive", 4.1, 155, 20, "MEDIUM"),
            new SupplierSeed("Sapphire Pharma Logistics", "Sapphire Pharma Logistics", "Aisha Khan", "care@sapphirepharma.com", "+44-20-7946-8122", "27 Canary Wharf", "UK", "London", "Pharma", 4.4, 120, 9, "LOW"),
            new SupplierSeed("Titan Packaging Solutions", "Titan Packaging Solutions", "Olivia Brown", "hello@titanpack.com", "+61-2-9188-7741", "77 King Street", "Australia", "Sydney", "Packaging", 4.0, 145, 16, "MEDIUM"),
            new SupplierSeed("Horizon Textiles", "Horizon Textiles", "Priya Singh", "service@horizontextiles.com", "+91-44-6001-8832", "12 ECR Road", "India", "Chennai", "Textiles", 3.9, 135, 22, "MEDIUM"),
            new SupplierSeed("Delta Precision Tools", "Delta Precision Tools", "Carlos Diaz", "tools@deltaprecision.mx", "+52-55-6200-4401", "18 Reforma Norte", "Mexico", "Mexico City", "Machinery", 4.2, 150, 17, "MEDIUM"),
            new SupplierSeed("Orion Medical Devices", "Orion Medical Devices", "Sara Johnson", "support@orionmedical.com", "+1-617-555-0172", "9 Cambridge Street", "USA", "Boston", "Medical", 4.9, 110, 4, "LOW"),
            new SupplierSeed("Zenith Battery Systems", "Zenith Battery Systems", "Min-jun Park", "battery@zenithsystems.kr", "+82-2-560-1414", "11 Seoul Tech Park", "South Korea", "Seoul", "Energy", 4.3, 170, 15, "MEDIUM"),
            new SupplierSeed("Nova Agro Trade", "Nova Agro Trade", "Fatima Ali", "trade@novaagro.com", "+971-4-555-7801", "33 Jebel Ali Free Zone", "UAE", "Dubai", "Agro", 4.0, 125, 19, "MEDIUM"),
            new SupplierSeed("Quantum Raw Materials", "Quantum Raw Materials", "David Wilson", "procurement@quantummaterials.com", "+1-713-555-0109", "222 Energy Corridor", "USA", "Houston", "Raw Materials", 3.8, 180, 26, "HIGH"),
            new SupplierSeed("Crestline Shipping Partners", "Crestline Shipping Partners", "Hana Suzuki", "partner@crestlineshipping.com", "+81-45-550-1200", "4 Minato Mirai", "Japan", "Yokohama", "Shipping", 4.5, 165, 13, "LOW"),
            new SupplierSeed("Alpine Machinery Works", "Alpine Machinery Works", "Nina Keller", "orders@alpineworks.ch", "+41-44-555-6622", "8 Bahnhofstrasse", "Switzerland", "Zurich", "Machinery", 4.7, 130, 7, "LOW"),
            new SupplierSeed("Solstice Consumer Goods", "Solstice Consumer Goods", "Imran Yusuf", "distribution@solsticegoods.com", "+65-6222-1103", "90 Marina Boulevard", "Singapore", "Singapore", "Consumer Goods", 4.1, 140, 18, "MEDIUM"),
            new SupplierSeed("Vertex Chemical Supplies", "Vertex Chemical Supplies", "Rachel Adams", "chem@vertexchemical.com", "+1-646-555-0151", "501 Hudson Street", "USA", "New York", "Chemicals", 3.7, 185, 29, "HIGH"),
            new SupplierSeed("Harbor Tech Distributors", "Harbor Tech Distributors", "Miguel Santos", "tech@harbortech.com", "+34-93-555-3301", "14 Port Avenue", "Spain", "Barcelona", "Technology", 4.4, 150, 11, "LOW")
    );

    private static final List<RouteSeed> ROUTES = List.of(
            new RouteSeed("Shanghai", "China", "Mumbai", "India", "Singapore Port", Shipment.Priority.HIGH, Shipment.ShipmentStatus.IN_TRANSIT, 16, 78, 74, 60, 650, 22000, "Heavy rainfall and port congestion on the route.", "Weather, port congestion, customs variability"),
            new RouteSeed("Hamburg", "Germany", "New York", "USA", "Atlantic Crossing", Shipment.Priority.MEDIUM, Shipment.ShipmentStatus.IN_TRANSIT, 14, 35, 48, 30, 520, 18000, "Stable ocean route with moderate customs timing risk.", "Ocean transit, customs clearance"),
            new RouteSeed("Mumbai", "India", "Dubai", "UAE", "Jebel Ali Hub", Shipment.Priority.LOW, Shipment.ShipmentStatus.PENDING, 7, 22, 24, 18, 310, 14500, "Short haul route with low disruption probability.", "Regional transport, warehouse availability"),
            new RouteSeed("Los Angeles", "USA", "Toronto", "Canada", "Chicago Transfer", Shipment.Priority.MEDIUM, Shipment.ShipmentStatus.CREATED, 11, 28, 38, 25, 430, 20500, "Cross-border movements require balanced routing.", "Border processing, carrier capacity"),
            new RouteSeed("Tokyo", "Japan", "Sydney", "Australia", "Pacific Control Tower", Shipment.Priority.HIGH, Shipment.ShipmentStatus.IN_TRANSIT, 15, 44, 52, 38, 490, 26000, "Weather-sensitive Pacific lane with longer transit.", "Weather, transshipment, schedule volatility"),
            new RouteSeed("Bengaluru", "India", "London", "UK", "Dubai Staging", Shipment.Priority.URGENT, Shipment.ShipmentStatus.DELAYED, 19, 82, 81, 72, 190, 87000, "High-value pharma lane with customs and weather exposure.", "Weather, customs, supplier delay"),
            new RouteSeed("Mexico City", "Mexico", "Chicago", "USA", "Dallas Cross-Dock", Shipment.Priority.MEDIUM, Shipment.ShipmentStatus.IN_TRANSIT, 9, 30, 42, 28, 600, 19000, "North American corridor with border checkpoint risk.", "Border processing, route complexity"),
            new RouteSeed("Seoul", "South Korea", "Rotterdam", "Netherlands", "Hamburg Feeder", Shipment.Priority.HIGH, Shipment.ShipmentStatus.IN_TRANSIT, 17, 50, 66, 44, 710, 33000, "Long-haul shipment with feeder-connection dependence.", "Carrier handoff, congestion, port scheduling"),
            new RouteSeed("Chennai", "India", "Paris", "France", "Mediterranean Hub", Shipment.Priority.URGENT, Shipment.ShipmentStatus.DELAYED, 18, 74, 77, 69, 260, 94000, "Critical lane currently exposed to rainfall and congestion.", "Weather, port congestion, inventory risk"),
            new RouteSeed("Singapore", "Singapore", "Frankfurt", "Germany", "Central Europe Rail", Shipment.Priority.MEDIUM, Shipment.ShipmentStatus.IN_TRANSIT, 13, 21, 41, 24, 350, 28000, "Efficient control-tower managed route.", "Intermodal transfer, rail scheduling"),
            new RouteSeed("Boston", "USA", "Dublin", "Ireland", "North Atlantic Hub", Shipment.Priority.MEDIUM, Shipment.ShipmentStatus.DELIVERED, 10, 16, 27, 18, 295, 16500, "Delivered with low disruption footprint.", "Carrier reliability, customs timing"),
            new RouteSeed("Yokohama", "Japan", "Los Angeles", "USA", "West Coast Port", Shipment.Priority.HIGH, Shipment.ShipmentStatus.IN_TRANSIT, 12, 41, 54, 39, 640, 41000, "Technology lane monitored for bottlenecks.", "Traffic, port congestion, supplier delay"),
            new RouteSeed("Houston", "USA", "Sao Paulo", "Brazil", "Panama Transfer", Shipment.Priority.MEDIUM, Shipment.ShipmentStatus.PENDING, 15, 32, 49, 33, 780, 30000, "Chemical shipment awaiting dispatch confirmation.", "Weather, documentation, carrier availability"),
            new RouteSeed("Barcelona", "Spain", "Dubai", "UAE", "Mediterranean Channel", Shipment.Priority.HIGH, Shipment.ShipmentStatus.IN_TRANSIT, 14, 38, 50, 31, 540, 27000, "High-value goods moving through a busy maritime corridor.", "Port congestion, customs, schedule risk"),
            new RouteSeed("Zurich", "Switzerland", "Chicago", "USA", "Air Freight Hub", Shipment.Priority.LOW, Shipment.ShipmentStatus.DELIVERED, 8, 14, 22, 12, 240, 15000, "High-reliability shipment delivered early.", "Air freight, low route complexity"),
            new RouteSeed("Sydney", "Australia", "Singapore", "Singapore", "Equatorial Lane", Shipment.Priority.MEDIUM, Shipment.ShipmentStatus.CREATED, 9, 26, 36, 20, 305, 17500, "Consumer goods shipment scheduled for cross-dock transfer.", "Sea lane, scheduling, demand volatility"),
            new RouteSeed("Rome", "Italy", "Toronto", "Canada", "Transatlantic Corridor", Shipment.Priority.HIGH, Shipment.ShipmentStatus.DELAYED, 16, 59, 68, 47, 450, 39000, "Machinery parts require extra handling and customs review.", "Handling, customs, weather"),
            new RouteSeed("Dubai", "UAE", "Berlin", "Germany", "European Freight Hub", Shipment.Priority.MEDIUM, Shipment.ShipmentStatus.CANCELLED, 12, 18, 30, 22, 370, 21000, "Cancelled after rescheduling window closed.", "Carrier reschedule, route reallocation"),
            new RouteSeed("New York", "USA", "Tokyo", "Japan", "Pacific Hub", Shipment.Priority.URGENT, Shipment.ShipmentStatus.IN_TRANSIT, 20, 68, 79, 66, 980, 105000, "Critical electronics shipment with elevated monitoring.", "Demand spikes, weather, customs"),
            new RouteSeed("Lisbon", "Portugal", "Singapore", "Singapore", "Maritime Corridor", Shipment.Priority.MEDIUM, Shipment.ShipmentStatus.IN_TRANSIT, 13, 24, 35, 19, 410, 23000, "Smooth maritime shipment for consumer packaging.", "Ocean leg, dispatch timing")
    );

    private static final String[] CARGO_TYPES = {
            "Electronics", "Automotive Parts", "Pharmaceuticals", "Consumer Goods", "Industrial Components",
            "Semiconductors", "Packaging Materials", "Textiles", "Machinery Parts", "Raw Materials"
    };

    // Product seed data
    private static final List<ProductSeed> PRODUCT_SEEDS = List.of(
            new ProductSeed("CPU-2026-001", "Intel Core i9 Processor", "Electronics", "High-performance CPU for servers", 450.00, "unit", 0.5),
            new ProductSeed("CPU-2026-002", "AMD Ryzen 9 Processor", "Electronics", "High-performance CPU for workstations", 420.00, "unit", 0.5),
            new ProductSeed("GPU-2026-001", "NVIDIA RTX 4090", "Electronics", "Graphics card for AI workloads", 1599.00, "unit", 2.1),
            new ProductSeed("MEM-2026-001", "64GB DDR5 RAM", "Electronics", "Server memory module", 280.00, "unit", 0.1),
            new ProductSeed("SSD-2026-001", "2TB NVMe SSD", "Electronics", "High-speed storage", 180.00, "unit", 0.05),
            new ProductSeed("ENG-2026-001", "V8 Engine Block", "Automotive", "Complete engine assembly", 3500.00, "unit", 120.0),
            new ProductSeed("TRN-2026-001", "10-Speed Transmission", "Automotive", "Automatic transmission", 2800.00, "unit", 85.0),
            new ProductSeed("BRK-2026-001", "Performance Brake Kit", "Automotive", "Ceramic brake system", 650.00, "set", 15.0),
            new ProductSeed("MED-2026-001", "Surgical Masks N95", "Medical", "Pack of 50 masks", 45.00, "box", 0.5),
            new ProductSeed("MED-2026-002", "IV Catheter Set", "Medical", "Sterile IV kit", 12.50, "unit", 0.1),
            new ProductSeed("PHR-2026-001", "Amoxicillin 500mg", "Pharmaceuticals", "Antibiotic - 100 tablets", 25.00, "bottle", 0.2),
            new ProductSeed("PHR-2026-002", "Ibuprofen 400mg", "Pharmaceuticals", "Pain reliever - 200 tablets", 15.00, "bottle", 0.3),
            new ProductSeed("IND-2026-001", "Industrial Bearing Set", "Industrial", "Heavy-duty bearing kit", 320.00, "set", 8.0),
            new ProductSeed("IND-2026-002", "Hydraulic Pump", "Industrial", "High-pressure hydraulic pump", 1200.00, "unit", 25.0),
            new ProductSeed("IND-2026-003", "Steel Ball Screws", "Industrial", "Precision ball screw assembly", 450.00, "unit", 12.0),
            new ProductSeed("TEX-2026-001", "Cotton Fabric Roll", "Textiles", "Premium cotton - 100m roll", 350.00, "roll", 25.0),
            new ProductSeed("TEX-2026-002", "Polyester Thread", "Textiles", "Industrial thread spool", 28.00, "spool", 0.5),
            new ProductSeed("PKG-2026-001", "Corrugated Boxes", "Packaging", "Pack of 100 boxes", 85.00, "pack", 15.0),
            new ProductSeed("PKG-2026-002", "Stretch Wrap Film", "Packaging", "Industrial wrap - 500m", 45.00, "roll", 8.0),
            new ProductSeed("RAW-2026-001", "Aluminum Ingots", "Raw Materials", "99.7% pure aluminum", 2500.00, "ton", 1000.0)
    );

    // Warehouse seed data
    private static final List<WarehouseSeed> WAREHOUSE_SEEDS = List.of(
            new WarehouseSeed("Main Distribution Center", "Shanghai", "China", "Shanghai", "88 Logistics Park Road", 50000, "Zhang Wei", "+86-21-5501-1200", "shanghai@nexusflow.com", "Distribution"),
            new WarehouseSeed("Mumbai Regional Hub", "Mumbai", "India", "Mumbai", "45 Industrial Estate", 35000, "Raj Patel", "+91-22-4100-2100", "mumbai@nexusflow.com", "Regional"),
            new WarehouseSeed("Hamburg European Center", "Hamburg", "Germany", "Hamburg", "22 Hafen City", 45000, "Klaus Schmidt", "+49-30-8844-3300", "hamburg@nexusflow.com", "Distribution"),
            new WarehouseSeed("Los Angeles West Coast Hub", "Los Angeles", "USA", "Los Angeles", "1500 Port Boulevard", 60000, "Michael Johnson", "+1-310-555-0150", "la@nexusflow.com", "Distribution"),
            new WarehouseSeed("Tokyo Pacific Center", "Tokyo", "Japan", "Tokyo", "8 Shibuya District", 40000, "Kenji Yamamoto", "+81-3-4580-7800", "tokyo@nexusflow.com", "Regional"),
            new WarehouseSeed("Dubai Middle East Hub", "Dubai", "UAE", "Dubai", "Jebel Ali Free Zone", 55000, "Ahmed Al-Rashid", "+971-4-555-7900", "dubai@nexusflow.com", "Distribution"),
            new WarehouseSeed("Singapore ASEAN Center", "Singapore", "Singapore", "Singapore", "90 Jurong Port Road", 42000, "Li Ming", "+65-6222-1200", "singapore@nexusflow.com", "Regional"),
            new WarehouseSeed("Chicago Central Hub", "Chicago", "USA", "Chicago", "750 Industrial Drive", 48000, "Sarah Williams", "+1-312-555-0200", "chicago@nexusflow.com", "Distribution"),
            new WarehouseSeed("London European Gateway", "London", "UK", "London", "27 Thames Gateway", 38000, "James Anderson", "+44-20-7946-8200", "london@nexusflow.com", "Regional"),
            new WarehouseSeed("Sydney Pacific Hub", "Sydney", "Australia", "Sydney", "120 Botany Road", 32000, "Emma Thompson", "+61-2-9188-7800", "sydney@nexusflow.com", "Regional")
    );

    private void ensureDemoProducts() {
        Set<String> existingSkus = new HashSet<>();
        for (Product product : productRepository.findAll()) {
            if (product.getSku() != null) {
                existingSkus.add(product.getSku().toLowerCase());
            }
        }

        LocalDateTime now = LocalDateTime.now();
        for (ProductSeed seed : PRODUCT_SEEDS) {
            if (existingSkus.contains(seed.sku().toLowerCase())) {
                continue;
            }

            Product product = new Product();
            product.setSku(seed.sku());
            product.setProductName(seed.name());
            product.setCategory(seed.category());
            product.setDescription(seed.description());
            product.setUnitPrice(BigDecimal.valueOf(seed.price()).setScale(2, RoundingMode.HALF_UP));
            product.setUnitOfMeasure(seed.unitOfMeasure());
            product.setWeightPerUnit(BigDecimal.valueOf(seed.weight()).setScale(2, RoundingMode.HALF_UP));
            product.setIsActive(true);
            product.setCreatedAt(now);
            product.setUpdatedAt(now);

            productRepository.save(product);
        }
    }

    private void ensureDemoWarehouses() {
        Set<String> existingNames = new HashSet<>();
        for (Warehouse warehouse : warehouseRepository.findAll()) {
            if (warehouse.getWarehouseName() != null) {
                existingNames.add(warehouse.getWarehouseName().toLowerCase());
            }
        }

        LocalDateTime now = LocalDateTime.now();
        for (WarehouseSeed seed : WAREHOUSE_SEEDS) {
            if (existingNames.contains(seed.name().toLowerCase())) {
                continue;
            }

            Warehouse warehouse = new Warehouse();
            warehouse.setWarehouseName(seed.name());
            warehouse.setLocation(seed.location());
            warehouse.setCountry(seed.country());
            warehouse.setCity(seed.city());
            warehouse.setAddress(seed.address());
            warehouse.setCapacity(seed.capacity());
            warehouse.setManagerName(seed.managerName());
            warehouse.setContactPhone(seed.phone());
            warehouse.setContactEmail(seed.email());
            warehouse.setWarehouseType(seed.type());
            warehouse.setIsActive(true);
            warehouse.setCreatedAt(now);
            warehouse.setUpdatedAt(now);

            warehouseRepository.save(warehouse);
        }
    }

    private void ensureDemoInventory() {
        List<Product> products = new ArrayList<>(productRepository.findByIsActiveTrueOrderByProductNameAsc());
        List<Warehouse> warehouses = new ArrayList<>(warehouseRepository.findByIsActiveTrueOrderByWarehouseNameAsc());

        if (products.isEmpty() || warehouses.isEmpty()) {
            return;
        }

        // Create inventory for each product in 2-3 warehouses
        Set<String> existingInventory = new HashSet<>();
        for (Inventory inv : inventoryRepository.findAll()) {
            String key = inv.getProduct().getId() + "-" + inv.getWarehouse().getId();
            existingInventory.add(key);
        }

        LocalDateTime now = LocalDateTime.now();
        for (Product product : products) {
            // Assign to random warehouses
            int numWarehouses = 2 + (product.hashCode() % 3);
            for (int i = 0; i < numWarehouses && i < warehouses.size(); i++) {
                Warehouse warehouse = warehouses.get((product.hashCode() + i) % warehouses.size());
                String key = product.getId() + "-" + warehouse.getId();
                if (existingInventory.contains(key)) {
                    continue;
                }

                Inventory inventory = new Inventory();
                inventory.setProduct(product);
                inventory.setWarehouse(warehouse);

                int baseQty = 50 + (product.hashCode() % 500);
                inventory.setAvailableQuantity(baseQty);
                inventory.setReservedQuantity(baseQty / 10);
                inventory.setMinimumStockLevel(baseQty / 5);
                inventory.setMaximumStockLevel(baseQty * 3);
                inventory.setReorderPoint(baseQty / 4);
                inventory.setUnitCost(product.getUnitPrice() != null ? product.getUnitPrice().multiply(BigDecimal.valueOf(0.7)) : BigDecimal.ZERO);
                inventory.setLastStockCheck(now);
                inventory.setIsActive(true);
                inventory.setCreatedAt(now);
                inventory.setUpdatedAt(now);

                // Calculate total value
                if (inventory.getUnitCost() != null) {
                    inventory.setTotalValue(inventory.getUnitCost()
                            .multiply(BigDecimal.valueOf(inventory.getAvailableQuantity())));
                }

                inventoryRepository.save(inventory);
                existingInventory.add(key);
            }
        }
    }

    private record ProductSeed(
            String sku,
            String name,
            String category,
            String description,
            double price,
            String unitOfMeasure,
            double weight
    ) {}

    private record WarehouseSeed(
            String name,
            String location,
            String country,
            String city,
            String address,
            int capacity,
            String managerName,
            String phone,
            String email,
            String type
    ) {}
}
