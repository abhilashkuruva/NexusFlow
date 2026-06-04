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

-- Insert sample shipments
INSERT IGNORE INTO shipments (id, tracking_number, supplier_id, origin_city, origin_country, destination_city, destination_country, shipment_date, estimated_delivery_date, status, cargo_type, weight_kg, value_usd, priority, notes)
VALUES 
(1, 'TRK-2024-001', 1, 'San Francisco', 'USA', 'London', 'UK', '2024-01-15', '2024-01-25', 'DELIVERED', 'Electronics', 500.00, 25000.00, 'MEDIUM', 'Standard electronics shipment'),
(2, 'TRK-2024-002', 2, 'Shanghai', 'China', 'Los Angeles', 'USA', '2024-01-20', '2024-02-05', 'IN_TRANSIT', 'Machinery Parts', 1200.00, 45000.00, 'HIGH', 'Urgent machinery components'),
(3, 'TRK-2024-003', 3, 'Berlin', 'Germany', 'New York', 'USA', '2024-01-22', '2024-02-01', 'DELAYED', 'Automotive Parts', 800.00, 35000.00, 'HIGH', 'Customs delay expected'),
(4, 'TRK-2024-004', 4, 'Mumbai', 'India', 'Dubai', 'UAE', '2024-01-25', '2024-02-10', 'PENDING', 'Textiles', 300.00, 15000.00, 'LOW', 'Textile shipment pending pickup'),
(5, 'TRK-2024-005', 5, 'Mexico City', 'Mexico', 'Chicago', 'USA', '2024-01-28', '2024-02-08', 'IN_TRANSIT', 'Food Products', 600.00, 20000.00, 'MEDIUM', 'Perishable goods - temperature controlled');