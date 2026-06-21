-- ============================================================
-- NexusFlow Database Setup Script (CORRECTED)
-- ============================================================

DROP VIEW IF EXISTS shipment_summary;

DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS delay_predictions;
DROP TABLE IF EXISTS risk_scores;
DROP TABLE IF EXISTS shipment_routes;
DROP TABLE IF EXISTS shipments;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;

-- ============================================================
-- USERS TABLE
-- ============================================================

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'ANALYST', 'MANAGER') NOT NULL DEFAULT 'ANALYST',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,

    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
);

-- ============================================================
-- SUPPLIERS TABLE
-- ============================================================

CREATE TABLE suppliers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    country VARCHAR(100) NOT NULL,

    -- FIXED DECIMAL SIZE
    reliability_score DECIMAL(4,2) DEFAULT 5.00,

    total_shipments INT DEFAULT 0,
    delayed_shipments INT DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    is_active BOOLEAN DEFAULT TRUE,

    INDEX idx_suppliers_country (country),
    INDEX idx_suppliers_reliability (reliability_score)
);

-- ============================================================
-- SHIPMENTS TABLE
-- ============================================================

CREATE TABLE shipments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    tracking_number VARCHAR(50) NOT NULL UNIQUE,

    supplier_id BIGINT NOT NULL,

    origin_city VARCHAR(100) NOT NULL,
    origin_country VARCHAR(100) NOT NULL,

    destination_city VARCHAR(100) NOT NULL,
    destination_country VARCHAR(100) NOT NULL,

    shipment_date DATE NOT NULL,
    estimated_delivery_date DATE NOT NULL,
    actual_delivery_date DATE,

    status ENUM(
        'PENDING',
        'IN_TRANSIT',
        'DELAYED',
        'DELIVERED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PENDING',

    cargo_type VARCHAR(100),

    weight_kg DECIMAL(10,2),
    value_usd DECIMAL(12,2),

    priority ENUM(
        'LOW',
        'MEDIUM',
        'HIGH',
        'URGENT'
    ) DEFAULT 'MEDIUM',

    notes TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_shipments_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id)
        ON DELETE CASCADE,

    INDEX idx_shipments_tracking (tracking_number),
    INDEX idx_shipments_supplier (supplier_id),
    INDEX idx_shipments_status (status),
    INDEX idx_shipments_dates (shipment_date, estimated_delivery_date)
);

-- ============================================================
-- SHIPMENT ROUTES TABLE
-- ============================================================

CREATE TABLE shipment_routes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    shipment_id BIGINT NOT NULL,

    checkpoint_name VARCHAR(255) NOT NULL,
    checkpoint_city VARCHAR(100),
    checkpoint_country VARCHAR(100),

    arrival_time TIMESTAMP NULL,
    departure_time TIMESTAMP NULL,

    status ENUM(
        'PENDING',
        'ARRIVED',
        'DEPARTED',
        'SKIPPED'
    ) DEFAULT 'PENDING',

    notes TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_routes_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES shipments(id)
        ON DELETE CASCADE,

    INDEX idx_routes_shipment (shipment_id),
    INDEX idx_routes_status (status)
);

-- ============================================================
-- RISK SCORES TABLE
-- ============================================================

CREATE TABLE risk_scores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    shipment_id BIGINT NOT NULL UNIQUE,

    risk_level ENUM(
        'LOW',
        'MEDIUM',
        'HIGH',
        'CRITICAL'
    ) NOT NULL,

    risk_score DECIMAL(5,2) NOT NULL,
    delay_probability DECIMAL(5,2) NOT NULL,

    factors TEXT,

    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_risk_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES shipments(id)
        ON DELETE CASCADE,

    INDEX idx_risk_shipment (shipment_id),
    INDEX idx_risk_level (risk_level),
    INDEX idx_risk_score (risk_score)
);

-- ============================================================
-- DELAY PREDICTIONS TABLE
-- ============================================================

CREATE TABLE delay_predictions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    shipment_id BIGINT NOT NULL UNIQUE,

    predicted_delay_hours INT,
    confidence_score DECIMAL(5,2),

    prediction_reason TEXT,

    is_delayed BOOLEAN DEFAULT FALSE,

    predicted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_prediction_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES shipments(id)
        ON DELETE CASCADE,

    INDEX idx_prediction_shipment (shipment_id),
    INDEX idx_prediction_delayed (is_delayed)
);

-- ============================================================
-- NOTIFICATIONS TABLE
-- ============================================================

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,

    type ENUM(
        'INFO',
        'WARNING',
        'ALERT',
        'SUCCESS'
    ) NOT NULL DEFAULT 'INFO',

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
    'analyst@nexusflow.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiH6Hn7TRQ6jMnIKh4j/wQUp3NEEraW',
    'Data',
    'Analyst',
    'ANALYST',
    TRUE
),

(
    'manager@nexusflow.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiH6Hn7TRQ6jMnIKh4j/wQUp3NEEraW',
    'Project',
    'Manager',
    'MANAGER',
    TRUE
);

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
    is_active
) VALUES

('GlobalTech Components', 'John Smith', 'john@globaltech.com', '+1-555-0101', '123 Tech Park, Silicon Valley', 'USA', 4.50, 150, 12, TRUE),
('Asia Manufacturing Ltd', 'Li Wei', 'liwei@asiamanuf.cn', '+86-10-12345678', '456 Industrial Zone, Shanghai', 'China', 4.20, 200, 25, TRUE),
('EuroParts GmbH', 'Hans Mueller', 'hans@europarts.de', '+49-30-987654', '789 Auto Strasse, Munich', 'Germany', 4.80, 120, 5, TRUE),
('Indian Textiles Co', 'Priya Sharma', 'priya@indiantextiles.in', '+91-22-1234567', '321 Textile Market, Mumbai', 'India', 3.90, 180, 35, TRUE);

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
    risk_score,
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
(20, 'MEDIUM', 47.00, 34.00, 'Pending pickup and scheduling'),
(21, 'LOW', 18.00, 12.00, 'Standard lane performance'),
(22, 'MEDIUM', 46.00, 35.00, 'Mixed conditions'),
(23, 'HIGH', 72.00, 55.00, 'Unstable routing'),
(24, 'LOW', 24.00, 17.00, 'Good supplier reliability'),
(25, 'CRITICAL', 94.00, 86.00, 'Urgent shipment with low buffer');


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
    rs.risk_score,

    dp.predicted_delay_hours,
    dp.is_delayed AS prediction_delayed

FROM shipments s

JOIN suppliers sup
    ON s.supplier_id = sup.id

LEFT JOIN risk_scores rs
    ON s.id = rs.shipment_id

LEFT JOIN delay_predictions dp
    ON s.id = dp.shipment_id;