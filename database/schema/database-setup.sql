-- ============================================================
-- NexusFlow Normalized Database Design
-- ============================================================
-- This script is intended to be run by Spring Boot's DDL auto feature
-- or manually to set up the initial schema.
-- It should NOT contain DROP DATABASE or CREATE DATABASE statements
-- if Spring Boot is configured to manage the schema.

-- Drop tables in reverse order of dependency
DROP VIEW IF EXISTS shipment_summary;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS delay_predictions;
DROP TABLE IF EXISTS risk_scores;
DROP TABLE IF EXISTS shipment_tracking;
DROP TABLE IF EXISTS shipments;
DROP TABLE IF EXISTS inventory;

DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS supplier_performance;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles; -- Drop roles last if it exists
DROP TABLE IF EXISTS demand_history;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS weather_data;

-- PRD: Inventory risk fields will be added below via DDL auto / schema update


-- PRD core tables (added for AI-powered risk intelligence)
DROP TABLE IF EXISTS risk_events;
DROP TABLE IF EXISTS recommendations;
DROP TABLE IF EXISTS predictions;

-- ============================================================
-- PRD: RISK EVENTS / INTELLIGENCE WORKFLOW
-- ============================================================

CREATE TABLE risk_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    risk_type VARCHAR(80) NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,

    affected_entity_type VARCHAR(80) NOT NULL,
    affected_entity_id BIGINT,

    impact_assessment TEXT,

    recommended_action TEXT,

    status ENUM('NEW', 'ACKNOWLEDGED', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'NEW',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- PRD: AI PREDICTIONS (horizon-based)
-- ============================================================

CREATE TABLE predictions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    horizon_days INT NOT NULL,

    related_supplier_id BIGINT NULL,
    related_shipment_id BIGINT NULL,
    related_product_id BIGINT NULL,

    probability_pct DECIMAL(5,2) NOT NULL,

    prediction_class ENUM('PRODUCTION_DELAY', 'SHIPMENT_DELAY', 'INVENTORY_SHORTAGE', 'SUPPLIER_FAILURE', 'GEOPOLITICAL_DISRUPTION') NOT NULL,

    confidence_score DECIMAL(5,2) NOT NULL,

    explanation TEXT NOT NULL,

    prediction_reason TEXT,

    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uq_prediction_horizon_entities (horizon_days, related_supplier_id, related_shipment_id, related_product_id)
);

-- ============================================================
-- PRD: AI RECOMMENDATIONS
-- ============================================================

CREATE TABLE recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    recommendation_type ENUM('SWITCH_SUPPLIER_ALLOCATION', 'INCREASE_SAFETY_STOCK', 'CHANGE_TRANSPORT_ROUTE', 'ACTIVATE_BACKUP_SUPPLIER', 'OTHER') NOT NULL DEFAULT 'OTHER',

    target_entity_type VARCHAR(80) NOT NULL,
    target_entity_id BIGINT,

    recommendation_text TEXT NOT NULL,

    parameters JSON,

    status ENUM('ACTIVE', 'STALE', 'IMPLEMENTED', 'DISMISSED') NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- USERS TABLE
-- ============================================================



CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'SUPPLY_MANAGER', 'LOGISTICS_MANAGER', 'SUPPLIER', 'ANALYST') NOT NULL DEFAULT 'SUPPLY_MANAGER',
    role ENUM('ADMIN', 'SUPPLY_MANAGER', 'LOGISTICS_MANAGER', 'SUPPLIER', 'ANALYST') NOT NULL DEFAULT 'SUPPLY_MANAGER', -- No change needed, already correct
    phone VARCHAR(20),
    company_name VARCHAR(150),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
);

-- ============================================================
-- SUPPLIERS TABLE
-- ============================================================
CREATE TABLE suppliers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,

    contact_person VARCHAR(255),
    company_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    country VARCHAR(100),
    location VARCHAR(100),
    industry VARCHAR(100),

    -- PRD supplier reliability + risk model inputs (0-100 scores)
    reliability_score DECIMAL(5,2) DEFAULT 0.00,
    delivery_performance_score DECIMAL(5,2) DEFAULT 0.00,
    quality_score DECIMAL(5,2) DEFAULT 0.00,
    financial_stability_score DECIMAL(5,2) DEFAULT 0.00,
    geopolitical_exposure_score DECIMAL(5,2) DEFAULT 0.00,
    historical_reliability_score DECIMAL(5,2) DEFAULT 0.00,

    supplier_risk_score DECIMAL(5,2) DEFAULT 0.00,
    risk_level VARCHAR(20),

    total_shipments INT DEFAULT 0,
    delayed_shipments INT DEFAULT 0,

    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_suppliers_name (name)
    INDEX idx_suppliers_name (name) -- No change needed, already correct
);


-- TABLE: supplier_performance
CREATE TABLE supplier_performance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    supplier_id BIGINT,
    total_orders INT DEFAULT 0,
    completed_orders INT DEFAULT 0,
    delayed_orders INT DEFAULT 0,
    average_delivery_time DECIMAL(10,2),
    performance_score DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE
);

-- TABLE: products
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    unit_price DECIMAL(12,2) DEFAULT 0.00,
    supplier_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- TABLE: inventory
CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    available_quantity INT DEFAULT 0,
    reserved_quantity INT DEFAULT 0,
    minimum_stock_level INT DEFAULT 10,
    maximum_stock_level INT DEFAULT 0,
    reorder_point INT DEFAULT 0,
    unit_cost DECIMAL(12,2) DEFAULT 0.00,
    total_value DECIMAL(14,2) DEFAULT 0.00,
    last_stock_check TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventory_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
    UNIQUE KEY uq_inventory_product_warehouse (product_id, warehouse_id)
);


-- TABLE: shipments
CREATE TABLE shipments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tracking_number VARCHAR(50) UNIQUE NOT NULL,
    tracking_number VARCHAR(50) UNIQUE NOT NULL, -- No change needed, already correct
    supplier_id BIGINT NOT NULL,
    product_id BIGINT,
    origin_city VARCHAR(100) NOT NULL,
    origin_country VARCHAR(100) NOT NULL,
    destination_city VARCHAR(100) NOT NULL,
    destination_country VARCHAR(100) NOT NULL,
    quantity INT,
    current_location VARCHAR(255),
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    weather_impact_score DECIMAL(5,2) DEFAULT 0.00,
    route_complexity_score DECIMAL(5,2) DEFAULT 0.00,
    inventory_risk_score DECIMAL(5,2) DEFAULT 0.00,
    status ENUM('CREATED', 'IN_TRANSIT', 'DELAYED', 'DELIVERED', 'CANCELLED', 'PENDING') DEFAULT 'PENDING',
    cargo_type VARCHAR(100),
    weight_kg DECIMAL(10,2),
    value_usd DECIMAL(12,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_shipments_number (tracking_number),
    INDEX idx_shipments_number (tracking_number), -- No change needed, already correct
    INDEX idx_shipments_supplier (supplier_id),
    INDEX idx_shipments_status (status)
);

-- TABLE: shipment_tracking
CREATE TABLE shipment_tracking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shipment_id BIGINT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    current_location VARCHAR(255),
    estimated_arrival TIMESTAMP,
    tracking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
);

-- TABLE: routes
CREATE TABLE routes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source VARCHAR(255),
    destination VARCHAR(255),
    distance DECIMAL(10,2),
    risk_factor DECIMAL(5,2),
    estimated_time INT -- in minutes
);

-- TABLE: risk_scores
CREATE TABLE risk_scores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shipment_id BIGINT UNIQUE,
    supplier_risk DECIMAL(5,2),
    delay_probability DECIMAL(5,2),
    weather_risk DECIMAL(5,2),
    route_risk DECIMAL(5,2),
    inventory_risk DECIMAL(5,2),
    overall_score DECIMAL(5,2),
    risk_level ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    generated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    factors TEXT,
    factors TEXT, -- No change needed, already correct
    FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
);

-- TABLE: weather_data
CREATE TABLE weather_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    location VARCHAR(255),
    temperature DECIMAL(5,2),
    rainfall DECIMAL(5,2),
    storm_probability DECIMAL(5,2),
    risk_level VARCHAR(20),
    recorded_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- ANALYTICS TABLES
-- ============================================================

CREATE TABLE demand_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    month_date DATE NOT NULL,
    actual_demand INT NOT NULL,
    predicted_demand INT,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action VARCHAR(255),
    entity_type VARCHAR(100),
    entity_id BIGINT,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ============================================================
-- NOTIFICATIONS TABLE
-- ============================================================

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,

    type ENUM ('INFO', 'WARNING', 'ALERT', 'SUCCESS') NOT NULL DEFAULT 'INFO',

    is_read BOOLEAN DEFAULT FALSE,

    related_shipment_id BIGINT NULL,
    related_supplier_id BIGINT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_notifications_shipment
        FOREIGN KEY (related_shipment_id)
        REFERENCES shipments(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_notifications_supplier
        FOREIGN KEY (related_supplier_id)
        REFERENCES suppliers(id)
        ON DELETE SET NULL,

    INDEX idx_notifications_user (user_id),
    INDEX idx_notifications_read (is_read),
    INDEX idx_notifications_type (type)
);

-- ============================================================
-- USERS SEED DATA
-- ============================================================
-- PASSWORD FOR ALL USERS: admin123
-- ============================================================

INSERT INTO users (
    email,
    password,
    first_name,
    last_name,
    role,
    is_active
) VALUES
(
    'admin@nexusflow.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiH6Hn7TRQ6jMnIKh4j/wQUp3NEEraW',
    'Admin',
    'User',
    'ADMIN',
    TRUE
),
(
    'manager@nexusflow.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiH6Hn7TRQ6jMnIKh4j/wQUp3NEEraW',
    'SupplyChain',
    'Manager',
    'SUPPLY_MANAGER',
    TRUE
),
(
    'logistics@nexusflow.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiH6Hn7TRQ6jMnIKh4j/wQUp3NEEraW',
    'Logistics',
    'User',
    'LOGISTICS_MANAGER',
    TRUE
),
(
    'supplier@nexusflow.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiH6Hn7TRQ6jMnIKh4j/wQUp3NEEraW',
    'Supplier',
    'User',
    'SUPPLIER',
    TRUE
);

INSERT INTO products (sku, name, category, unit_price) VALUES 
('PROD-001', 'High-End Microchips', 'Electronics', 450.00),
('PROD-002', 'Steel Industrial Gears', 'Machinery', 1200.00);

-- NOTE: inventory seeding is done in data.sql to align with the current JPA schema (warehouse_id + stock fields).
-- Keeping this statement removed to avoid schema mismatches.


-- ============================================================
-- SUPPLIERS SEED DATA
-- ============================================================

INSERT INTO suppliers (
    name,
    contact_person,
    email,
    phone,
    address,
    country,
    reliability_score,
    total_shipments,
    delayed_shipments,
    is_active,
    industry
) VALUES

('GlobalTech Components', 'John Smith', 'john@globaltech.com', '+1-555-0101', '123 Tech Park, Silicon Valley', 'USA', 4.50, 150, 12, TRUE, 'Electronics'),
('Asia Manufacturing Ltd', 'Li Wei', 'liwei@asiamanuf.cn', '+86-10-12345678', '456 Industrial Zone, Shanghai', 'China', 4.20, 200, 25, TRUE, 'Manufacturing'),
('EuroParts GmbH', 'Hans Mueller', 'hans@europarts.de', '+49-30-987654', '789 Auto Strasse, Munich', 'Germany', 4.80, 120, 5, TRUE, 'Automotive'),
('Indian Textiles Co', 'Priya Sharma', 'priya@indiantextiles.in', '+91-22-1234567', '321 Textile Market, Mumbai', 'India', 3.90, 180, 35, TRUE, 'Textiles'),
('Latin American Traders', 'Maria Garcia', 'maria@latamtraders.com', '+52-55-1234-5678', '654 Comercio Blvd', 'Mexico', 4.1, 120, 18, TRUE, 'Food & Beverage'),
('African Minerals Inc', 'Kwame Nkrumah', 'kwame@africanminerals.gh', '+233-24-1234567', 'Mining District, Accra', 'Ghana', 4.0, 80, 10, TRUE, 'Mining'),
('Oceanic Seafoods', 'Aisha Khan', 'aisha@oceanic.au', '+61-2-1234-5678', 'Fisherman''s Wharf, Sydney', 'Australia', 4.7, 90, 3, TRUE, 'Food & Beverage'),
('Nordic Robotics', 'Bjorn Svensson', 'bjorn@nordicrobo.se', '+46-8-1234567', 'Innovation Hub, Stockholm', 'Sweden', 4.9, 70, 2, TRUE, 'Robotics');

-- ============================================================
-- SHIPMENTS SEED DATA
-- ============================================================

INSERT INTO shipments (
    tracking_number,
    supplier_id,
    origin_city,
    origin_country,
    destination_city,
    destination_country,
    shipment_date,
    estimated_delivery_date,
    actual_delivery_date,
    status,
    cargo_type,
    weight_kg,
    value_usd,
    priority
) VALUES

('NFS-2024-001', 1, 'San Francisco', 'USA', 'London', 'UK', '2024-01-15', '2024-01-25', '2024-01-24', 'DELIVERED', 'Electronics', 500.00, 75000.00, 'HIGH'),
('NFS-2024-002', 2, 'Shanghai', 'China', 'Los Angeles', 'USA', '2024-01-18', '2024-02-01', '2024-02-05', 'DELAYED', 'Machinery Parts', 1200.00, 45000.00, 'MEDIUM'),
('NFS-2024-003', 3, 'Munich', 'Germany', 'Detroit', 'USA', '2024-01-20', '2024-01-30', '2024-01-29', 'DELIVERED', 'Auto Parts', 800.00, 32000.00, 'HIGH'),
('NFS-2024-004', 4, 'Mumbai', 'India', 'Dubai', 'UAE', '2024-01-22', '2024-02-02', NULL, 'IN_TRANSIT', 'Textiles', 300.00, 15000.00, 'LOW'),
('NFS-2024-005', 1, 'Los Angeles', 'USA', 'Paris', 'France', '2024-01-24', '2024-02-08', '2024-02-14', 'DELAYED', 'Consumer Goods', 620.00, 22000.00, 'MEDIUM'),
('NFS-2024-006', 2, 'Tianjin', 'China', 'Toronto', 'Canada', '2024-01-26', '2024-02-10', NULL, 'IN_TRANSIT', 'Industrial Components', 980.00, 41000.00, 'HIGH'),
('NFS-2024-007', 3, 'Hamburg', 'Germany', 'Chicago', 'USA', '2024-01-28', '2024-02-12', '2024-02-13', 'DELAYED', 'Spare Parts', 450.00, 18000.00, 'LOW'),
('NFS-2024-008', 4, 'Bengaluru', 'India', 'Singapore', 'Singapore', '2024-01-29', '2024-02-15', NULL, 'PENDING', 'Pharmaceutical Supplies', 210.00, 88000.00, 'URGENT'),
('NFS-2024-009', 1, 'Seattle', 'USA', 'Madrid', 'Spain', '2024-02-01', '2024-02-18', NULL, 'IN_TRANSIT', 'Medical Equipment', 760.00, 67000.00, 'HIGH'),
('NFS-2024-010', 2, 'Shenzhen', 'China', 'New York', 'USA', '2024-02-03', '2024-02-20', '2024-02-28', 'DELAYED', 'Electronics', 540.00, 52000.00, 'URGENT'),
('NFS-2024-011', 3, 'Cologne', 'Germany', 'Boston', 'USA', '2024-02-05', '2024-02-22', '2024-02-21', 'DELIVERED', 'Automotive Components', 880.00, 36000.00, 'MEDIUM'),
('NFS-2024-012', 4, 'Hyderabad', 'India', 'Amsterdam', 'Netherlands', '2024-02-06', '2024-02-26', NULL, 'PENDING', 'Textiles', 330.00, 24000.00, 'LOW'),
('NFS-2024-013', 1, 'Austin', 'USA', 'Berlin', 'Germany', '2024-02-08', '2024-02-25', '2024-02-25', 'DELIVERED', 'Industrial Machinery', 1400.00, 98000.00, 'HIGH'),
('NFS-2024-014', 2, 'Qingdao', 'China', 'Sydney', 'Australia', '2024-02-10', '2024-03-02', '2024-03-10', 'DELAYED', 'Food Products', 520.00, 26000.00, 'MEDIUM'),
('NFS-2024-015', 3, 'Frankfurt', 'Germany', 'San Jose', 'USA', '2024-02-12', '2024-03-01', NULL, 'IN_TRANSIT', 'Tech Accessories', 260.00, 12000.00, 'LOW'),
('NFS-2024-016', 4, 'Chennai', 'India', 'London', 'UK', '2024-02-13', '2024-03-05', '2024-03-06', 'DELAYED', 'Pharmaceutical Supplies', 180.00, 99000.00, 'URGENT'),
('NFS-2024-017', 1, 'Chicago', 'USA', 'Warsaw', 'Poland', '2024-02-14', '2024-03-06', '2024-03-03', 'DELIVERED', 'Electronics', 670.00, 54000.00, 'MEDIUM'),
('NFS-2024-018', 2, 'Guangzhou', 'China', 'Rome', 'Italy', '2024-02-16', '2024-03-10', NULL, 'IN_TRANSIT', 'Machinery Parts', 1180.00, 78000.00, 'HIGH'),
('NFS-2024-019', 3, 'Munich', 'Germany', 'Los Angeles', 'USA', '2024-02-18', '2024-03-12', '2024-03-20', 'DELAYED', 'Auto Parts', 900.00, 41000.00, 'HIGH'),
('NFS-2024-020', 4, 'Kolkata', 'India', 'Toronto', 'Canada', '2024-02-19', '2024-03-15', NULL, 'PENDING', 'Textiles', 360.00, 19000.00, 'LOW');


-- ============================================================
-- RISK SCORES SEED DATA
-- ============================================================

INSERT INTO risk_scores (
    shipment_id,
    risk_level,
    overall_score,
    delay_probability,
    factors
) VALUES

(1, 'LOW', 12.00, 8.00, 'Reliable supplier and stable lane'),
(2, 'HIGH', 78.00, 60.00, 'Port congestion and customs inspection'),
(3, 'LOW', 18.00, 12.00, 'Consistent delivery history'),
(4, 'MEDIUM', 42.00, 32.00, 'Weather variability in transit'),
(5, 'MEDIUM', 55.00, 40.00, 'Carrier capacity constraints'),
(6, 'HIGH', 70.00, 58.00, 'High-volume season demand'),
(7, 'LOW', 25.00, 18.00, 'Short route with strong tracking'),
(8, 'CRITICAL', 92.00, 82.00, 'Urgent pharma lane with limited buffer'),
(9, 'MEDIUM', 48.00, 36.00, 'Border processing timing uncertainty'),
(10, 'CRITICAL', 96.00, 88.00, 'Electronics high-risk lane and peak holidays'),
(11, 'LOW', 20.00, 14.00, 'On-time track record'),
(12, 'MEDIUM', 45.00, 33.00, 'Textile handling variability'),
(13, 'LOW', 16.00, 11.00, 'Direct shipping schedule'),
(14, 'HIGH', 66.00, 52.00, 'Long-haul ocean leg with delays'),
(15, 'LOW', 28.00, 19.00, 'Low volatility route'),
(16, 'CRITICAL', 90.00, 81.00, 'Pharma lane and strict compliance checks'),
(17, 'MEDIUM', 50.00, 39.00, 'Traffic congestion in destination city'),
(18, 'HIGH', 73.00, 59.00, 'Machinery handling delays possible'),
(19, 'HIGH', 80.00, 63.00, 'Regional transport disruptions'),
(20, 'MEDIUM', 47.00, 34.00, 'Pending pickup and scheduling');


-- ============================================================
-- DELAY PREDICTIONS SEED DATA
-- ============================================================

INSERT INTO delay_predictions (
    shipment_id,
    predicted_delay_hours,
    confidence_score,
    prediction_reason,
    is_delayed
) VALUES

(1, 0, 95.00, 'Reliable supplier', FALSE),
(2, 48, 72.00, 'Customs delay possible', TRUE),
(3, 0, 92.00, 'Excellent supplier track record', FALSE),
(4, 18, 60.00, 'Cross-border scheduling gap', TRUE),
(5, 36, 68.00, 'Port handling congestion', TRUE),
(6, 0, 90.00, 'Stable production and dispatch', FALSE),
(7, 24, 74.00, 'Road transport constraints', TRUE),
(8, 60, 58.00, 'Carrier capacity shortage', TRUE),
(9, 12, 66.00, 'Warehouse processing variability', TRUE),
(10, 72, 70.00, 'Peak season demand + routing risk', TRUE),
(11, 0, 93.00, 'On-time delivery evidence', FALSE),
(12, 40, 61.00, 'Pending pickup / scheduling risk', TRUE),
(13, 0, 96.00, 'High reliability supplier lane', FALSE),
(14, 54, 69.00, 'Long-haul ocean delays', TRUE),
(15, 0, 88.00, 'Low-risk in-transit monitoring', FALSE),
(16, 80, 63.00, 'Strict compliance checks', TRUE),
(17, 0, 91.00, 'Consistent carrier performance', FALSE),
(18, 30, 67.00, 'Unstable handling conditions', TRUE),
(19, 66, 75.00, 'Regional disruptions expected', TRUE),
(20, 24, 59.00, 'Scheduling and dock availability risk', TRUE);


-- ============================================================
-- NOTIFICATIONS SEED DATA
-- ============================================================

INSERT INTO notifications (
    user_id,
    title,
    message,
    type,
    related_shipment_id,
    related_supplier_id,
    is_read
) VALUES

(1, 'System Welcome', 'Welcome to NexusFlow Risk Intelligence System', 'INFO', NULL, NULL, TRUE),
(2, 'High Risk Alert', 'Shipment NFS-2024-002 risk increased', 'ALERT', 2, NULL, FALSE),
(3, 'Delivery Complete', 'Shipment NFS-2024-003 delivered successfully', 'SUCCESS', 3, NULL, TRUE),
(1, 'Delay Prediction', 'NFS-2024-004 predicted delay with moderate confidence', 'WARNING', 4, NULL, FALSE),
(2, 'Carrier Constraint', 'NFS-2024-005 shows risk from port handling congestion', 'ALERT', 5, NULL, FALSE),
(1, 'In Transit Update', 'NFS-2024-006 is monitoring stable conditions', 'INFO', 6, NULL, TRUE),
(2, 'Road Transport Risk', 'NFS-2024-007 may be impacted by road transport constraints', 'WARNING', 7, NULL, FALSE),
(3, 'Capacity Shortage', 'NFS-2024-008 is at critical risk due to carrier capacity shortage', 'ALERT', 8, NULL, FALSE),
(1, 'Customs Timing', 'NFS-2024-009 border processing uncertainty detected', 'WARNING', 9, NULL, TRUE),
(2, 'Peak Season Routing', 'NFS-2024-010 is critical during peak season routing risks', 'ALERT', 10, NULL, FALSE),
(3, 'Delivery Completed', 'NFS-2024-011 delivered on time', 'SUCCESS', 11, NULL, TRUE),
(2, 'Pickup Scheduling', 'NFS-2024-012 has pickup/scheduling risk', 'WARNING', 12, NULL, FALSE),
(1, 'Low Risk Update', 'NFS-2024-013 remains low risk', 'INFO', 13, NULL, TRUE),
(2, 'Long-haul Delays', 'NFS-2024-014 predicted delay due to long-haul ocean leg', 'ALERT', 14, NULL, FALSE),
(3, 'In Transit Monitoring', 'NFS-2024-015 low-risk monitoring in progress', 'INFO', 15, NULL, TRUE),
(1, 'Compliance Checks', 'NFS-2024-016 requires strict compliance and shows critical delay risk', 'ALERT', 16, NULL, FALSE),
(2, 'On-time Evidence', 'NFS-2024-017 predicted to remain on schedule', 'SUCCESS', 17, NULL, TRUE),
(3, 'Handling Instability', 'NFS-2024-018 may be delayed due to unstable handling conditions', 'WARNING', 18, NULL, FALSE),
(1, 'Regional Disruption', 'NFS-2024-019 expects regional transport disruptions', 'ALERT', 19, NULL, FALSE),
(2, 'Dock Availability', 'NFS-2024-020 has scheduling and dock availability risk', 'WARNING', 20, NULL, TRUE),
(1, 'Forecast Update', 'NFS-2024-006 forecast updated: monitoring continues', 'INFO', 6, NULL, FALSE),
(2, 'Risk Escalation', 'NFS-2024-001 shows elevated risk for next leg', 'ALERT', 1, NULL, FALSE);



-- ============================================================
-- VIEW
-- ============================================================

CREATE VIEW shipment_summary AS

SELECT
    s.id,
    s.tracking_number,
    s.status,
    s.origin_city,
    s.origin_country,
    s.destination_city,
    s.destination_country,
    s.shipment_date,
    s.estimated_delivery_date,
    s.actual_delivery_date,
    s.cargo_type,
    s.weight_kg,
    s.value_usd,
    s.priority,

    sup.name AS supplier_name,
    sup.country AS supplier_country,
    sup.reliability_score,

    rs.risk_level,
    rs.overall_score,

    dp.predicted_delay_hours,
    dp.is_delayed AS prediction_delayed

FROM shipments s

JOIN suppliers sup
    ON s.supplier_id = sup.id

LEFT JOIN risk_scores rs
    ON s.id = rs.shipment_id

LEFT JOIN delay_predictions dp
    ON s.id = dp.shipment_id;
