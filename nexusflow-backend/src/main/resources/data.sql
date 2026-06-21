-- ============================================================
-- NexusFlow Sample Data
-- ============================================================
-- This script inserts sample data on application startup
-- Only runs if tables are empty (due to spring.sql.init.mode=always)
-- ============================================================

-- User seeding is handled by database-setup.sql to ensure a single source of truth for bcrypt hashes.
-- Keeping user rows out of this file prevents password hash conflicts that cause login failures.


-- Insert sample suppliers

-- NOTE:
-- Users are seeded by database-setup.sql. Keeping only non-user seed data here prevents
-- bcrypt hash conflicts that cause /api/auth/login to return 401.

INSERT IGNORE INTO suppliers (id, name, contact_person, email, phone, address, country, reliability_score, total_shipments, delayed_shipments, is_active)
VALUES 
(1, 'Global Tech Supplies', 'John Smith', 'john@globaltech.com', '+1-555-0101', '123 Tech Ave', 'USA', 4.5, 150, 12, TRUE),
(2, 'Asia Manufacturing Co', 'Li Wei', 'liwei@asiamanuf.com', '+86-138-0000-1234', '456 Industry Rd', 'China', 4.2, 200, 25, TRUE),
(3, 'European Logistics GmbH', 'Hans Mueller', 'hans@eurolog.de', '+49-30-12345678', '789 Logistics Str', 'Germany', 4.8, 100, 5, TRUE),
(4, 'Indian Exports Ltd', 'Raj Patel', 'raj@indianexp.in', '+91-98765-43210', '321 Export Zone', 'India', 3.9, 180, 35, TRUE),
(5, 'Latin American Traders', 'Maria Garcia', 'maria@latamtraders.com', '+52-55-1234-5678', '654 Comercio Blvd', 'Mexico', 4.1, 120, 18, TRUE);

-- Insert sample risk scores (20 total so dashboard charts have enough data)
INSERT IGNORE INTO risk_scores (shipment_id, risk_level, risk_score, delay_probability, factors)
VALUES
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

-- Insert sample delay predictions (20 total)
INSERT IGNORE INTO delay_predictions (shipment_id, predicted_delay_hours, confidence_score, prediction_reason, is_delayed)
VALUES
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

-- Insert sample notifications (20 total)
INSERT IGNORE INTO notifications (user_id, title, message, type, related_shipment_id, related_supplier_id, is_read)
VALUES
(1, 'System Welcome', 'Welcome to NexusFlow Risk Intelligence System', 'INFO', NULL, NULL, TRUE),
(2, 'High Risk Alert', 'Shipment TRK-2024-002 risk increased', 'ALERT', 2, NULL, FALSE),
(3, 'Delivery Complete', 'Shipment TRK-2024-003 delivered successfully', 'SUCCESS', 3, NULL, TRUE),
(1, 'Delay Prediction', 'TRK-2024-004 predicted delay with moderate confidence', 'WARNING', 4, NULL, FALSE),
(2, 'Carrier Constraint', 'TRK-2024-005 shows risk from port handling congestion', 'ALERT', 5, NULL, FALSE),
(1, 'In Transit Update', 'TRK-2024-006 is monitoring stable conditions', 'INFO', 6, NULL, TRUE),
(2, 'Road Transport Risk', 'TRK-2024-007 may be impacted by road transport constraints', 'WARNING', 7, NULL, FALSE),
(3, 'Capacity Shortage', 'TRK-2024-008 is at critical risk due to carrier capacity shortage', 'ALERT', 8, NULL, FALSE),
(1, 'Customs Timing', 'TRK-2024-009 border processing uncertainty detected', 'WARNING', 9, NULL, TRUE),
(2, 'Peak Season Routing', 'TRK-2024-010 is critical during peak season routing risks', 'ALERT', 10, NULL, FALSE),
(3, 'Delivery Completed', 'TRK-2024-011 delivered on time', 'SUCCESS', 11, NULL, TRUE),
(2, 'Pickup Scheduling', 'TRK-2024-012 has pickup/scheduling risk', 'WARNING', 12, NULL, FALSE),
(1, 'Low Risk Update', 'TRK-2024-013 remains low risk', 'INFO', 13, NULL, TRUE),
(2, 'Long-haul Delays', 'TRK-2024-014 predicted delay due to long-haul ocean leg', 'ALERT', 14, NULL, FALSE),
(3, 'In Transit Monitoring', 'TRK-2024-015 low-risk monitoring in progress', 'INFO', 15, NULL, TRUE),
(1, 'Compliance Checks', 'TRK-2024-016 requires strict compliance and shows critical delay risk', 'ALERT', 16, NULL, FALSE),
(2, 'On-time Evidence', 'TRK-2024-017 predicted to remain on schedule', 'SUCCESS', 17, NULL, TRUE),
(3, 'Handling Instability', 'TRK-2024-018 may be delayed due to unstable handling conditions', 'WARNING', 18, NULL, FALSE),
(1, 'Regional Disruption', 'TRK-2024-019 expects regional transport disruptions', 'ALERT', 19, NULL, FALSE),
(2, 'Dock Availability', 'TRK-2024-020 has scheduling and dock availability risk', 'WARNING', 20, NULL, TRUE);

-- Insert sample shipments (extend from 5 to 20)
INSERT IGNORE INTO shipments (id, tracking_number, supplier_id, origin_city, origin_country, destination_city, destination_country, shipment_date, estimated_delivery_date, status, cargo_type, weight_kg, value_usd, priority, notes)
VALUES 
(1, 'TRK-2024-001', 1, 'San Francisco', 'USA', 'London', 'UK', '2024-01-15', '2024-01-25', 'DELIVERED', 'Electronics', 500.00, 25000.00, 'MEDIUM', 'Standard electronics shipment'),
(2, 'TRK-2024-002', 2, 'Shanghai', 'China', 'Los Angeles', 'USA', '2024-01-20', '2024-02-05', 'IN_TRANSIT', 'Machinery Parts', 1200.00, 45000.00, 'HIGH', 'Urgent machinery components'),
(3, 'TRK-2024-003', 3, 'Berlin', 'Germany', 'New York', 'USA', '2024-01-22', '2024-02-01', 'DELAYED', 'Automotive Parts', 800.00, 35000.00, 'HIGH', 'Customs delay expected'),
(4, 'TRK-2024-004', 4, 'Mumbai', 'India', 'Dubai', 'UAE', '2024-01-25', '2024-02-10', 'PENDING', 'Textiles', 300.00, 15000.00, 'LOW', 'Textile shipment pending pickup'),
(5, 'TRK-2024-005', 5, 'Mexico City', 'Mexico', 'Chicago', 'USA', '2024-01-28', '2024-02-08', 'IN_TRANSIT', 'Food Products', 600.00, 20000.00, 'MEDIUM', 'Perishable goods - temperature controlled'),
(6, 'TRK-2024-006', 1, 'Los Angeles', 'USA', 'Paris', 'France', '2024-02-01', '2024-02-15', 'DELIVERED', 'Consumer Goods', 620.00, 22000.00, 'MEDIUM', 'Delivered with standard conditions'),
(7, 'TRK-2024-007', 2, 'Tianjin', 'China', 'Toronto', 'Canada', '2024-02-03', '2024-02-20', 'IN_TRANSIT', 'Industrial Components', 980.00, 41000.00, 'HIGH', 'Dispatch on schedule; monitoring'),
(8, 'TRK-2024-008', 3, 'Hamburg', 'Germany', 'Chicago', 'USA', '2024-02-06', '2024-02-25', 'DELAYED', 'Spare Parts', 450.00, 18000.00, 'LOW', 'Road leg delay likely'),
(9, 'TRK-2024-009', 4, 'Bengaluru', 'India', 'Singapore', 'Singapore', '2024-02-08', '2024-03-01', 'PENDING', 'Pharmaceutical Supplies', 210.00, 88000.00, 'URGENT', 'Pending customs clearance'),
(10, 'TRK-2024-010', 5, 'Seattle', 'USA', 'Madrid', 'Spain', '2024-02-10', '2024-03-05', 'IN_TRANSIT', 'Medical Equipment', 760.00, 67000.00, 'HIGH', 'Long ocean leg risk'),
(11, 'TRK-2024-011', 1, 'Austin', 'USA', 'Berlin', 'Germany', '2024-02-12', '2024-03-07', 'DELIVERED', 'Industrial Machinery', 1400.00, 98000.00, 'HIGH', 'Delivered ahead of estimate'),
(12, 'TRK-2024-012', 2, 'Qingdao', 'China', 'Sydney', 'Australia', '2024-02-15', '2024-03-12', 'DELAYED', 'Food Products', 520.00, 26000.00, 'MEDIUM', 'Expected port congestion'),
(13, 'TRK-2024-013', 3, 'Frankfurt', 'Germany', 'San Jose', 'USA', '2024-02-17', '2024-03-14', 'IN_TRANSIT', 'Tech Accessories', 260.00, 12000.00, 'LOW', 'Monitoring low-risk conditions'),
(14, 'TRK-2024-014', 4, 'Chennai', 'India', 'London', 'UK', '2024-02-19', '2024-03-18', 'DELAYED', 'Pharmaceutical Supplies', 180.00, 99000.00, 'URGENT', 'Compliance-related delay'),
(15, 'TRK-2024-015', 5, 'Chicago', 'USA', 'Warsaw', 'Poland', '2024-02-21', '2024-03-20', 'DELIVERED', 'Electronics', 670.00, 54000.00, 'MEDIUM', 'On-time verified'),
(16, 'TRK-2024-016', 1, 'Guangzhou', 'China', 'Rome', 'Italy', '2024-02-23', '2024-03-22', 'IN_TRANSIT', 'Machinery Parts', 1180.00, 78000.00, 'HIGH', 'Route instability'),
(17, 'TRK-2024-017', 2, 'Munich', 'Germany', 'Los Angeles', 'USA', '2024-02-24', '2024-03-23', 'DELAYED', 'Auto Parts', 900.00, 41000.00, 'HIGH', 'Transport disruption'),
(18, 'TRK-2024-018', 3, 'Kolkata', 'India', 'Toronto', 'Canada', '2024-02-25', '2024-03-25', 'PENDING', 'Textiles', 360.00, 19000.00, 'LOW', 'Waiting pickup'),
(19, 'TRK-2024-019', 4, 'Delhi', 'India', 'Dubai', 'UAE', '2024-02-26', '2024-03-28', 'IN_TRANSIT', 'Industrial Components', 990.00, 48000.00, 'HIGH', 'Dock availability risk'),
(20, 'TRK-2024-020', 5, 'San Diego', 'USA', 'Tokyo', 'Japan', '2024-02-27', '2024-03-30', 'IN_TRANSIT', 'Electronics', 540.00, 72000.00, 'URGENT', 'Peak season routing risk');
