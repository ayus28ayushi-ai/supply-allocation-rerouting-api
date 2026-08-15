-- ==============================================================================
-- Flyway Migration: V2__dummy_data.sql
-- Seed Data for Testing & Demonstration
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- 1. USERS
-- Password for all test users is: password123 (BCrypt Hashed)
-- ------------------------------------------------------------------------------
INSERT INTO users (username, email, password, provider, role) VALUES
                                                                  ('mumbai_manager', 'mgr.mumbai@triage.com', '$2a$12$ASYQ6v41f30/xabA/XdaoeP8We9mxsEwoh7CzacjBexZb7rDsB79u', 'LOCAL', 'ROLE_USER'),
                                                                  ('bengaluru_manager', 'mgr.blr@triage.com', '$2a$12$ASYQ6v41f30/xabA/XdaoeP8We9mxsEwoh7CzacjBexZb7rDsB79u', 'LOCAL', 'ROLE_USER'),
                                                                  ('dr_smith', 'smith@cityhospital.org', '$2a$12$ASYQ6v41f30/xabA/XdaoeP8We9mxsEwoh7CzacjBexZb7rDsB79u', 'LOCAL', 'ROLE_USER'),
                                                                  ('priya_clinic', 'priya@metroclinic.io', '$2a$12$ASYQ6v41f30/xabA/XdaoeP8We9mxsEwoh7CzacjBexZb7rDsB79u', 'GOOGLE', 'ROLE_USER');

-- ------------------------------------------------------------------------------
-- 2. WAREHOUSES
-- ------------------------------------------------------------------------------
INSERT INTO warehouses (id, name, latitude, longitude, is_active, version) VALUES
                                                                               (1, 'Central Hub Mumbai', 19.0760, 72.8777, TRUE, 0),
                                                                               (2, 'North Depot Delhi', 28.7041, 77.1025, TRUE, 0),
                                                                               (3, 'South Logistics Bengaluru', 12.9716, 77.5946, TRUE, 0),
                                                                               (4, 'East Coast Kolkata', 22.5726, 88.3639, TRUE, 0),
                                                                               (5, 'Reserve Depot Chennai', 13.0827, 80.2707, FALSE, 0); -- Inactive warehouse test case

-- Sync PostgreSQL primary key sequence for warehouses
SELECT setval('warehouses_id_seq', (SELECT MAX(id) FROM warehouses));

-- ------------------------------------------------------------------------------
-- 3. INVENTORY ITEMS
-- ------------------------------------------------------------------------------
INSERT INTO inventory_items (id, warehouse_id, item_name, quantity_available, version) VALUES
-- Mumbai (WH 1)
(1, 1, 'Oxygen Concentrator 10L', 45, 0),
(2, 1, 'N95 Masks (Box of 50)', 500, 0),
(3, 1, 'Surgical Gloves (Box of 100)', 1200, 0),
(4, 1, 'Infrared Thermometers', 150, 0),

-- Delhi (WH 2)
(5, 2, 'Oxygen Concentrator 10L', 8, 0),
(6, 2, 'N95 Masks (Box of 50)', 350, 0),
(7, 2, 'Emergency First Aid Kit', 80, 0),

-- Bengaluru (WH 3)
(8, 3, 'Oxygen Concentrator 10L', 90, 0),
(9, 3, 'Surgical Gloves (Box of 100)', 850, 0),
(10, 3, 'Emergency First Aid Kit', 200, 0),

-- Kolkata (WH 4)
(11, 4, 'N95 Masks (Box of 50)', 150, 0),
(12, 4, 'Infrared Thermometers', 40, 0);

-- Sync PostgreSQL primary key sequence for inventory_items
SELECT setval('inventory_items_id_seq', (SELECT MAX(id) FROM inventory_items));

-- ------------------------------------------------------------------------------
-- 4. ALLOCATION RECORDS
-- ------------------------------------------------------------------------------
INSERT INTO allocation_records
(item_id, item_name, requester_name, quantity_claimed, requested_war_id, requested_war_name, fulfilled_war_id, fulfilled_war_name, is_rerouted, distance_km, timestamp, is_active, cancelled_by, version)
VALUES
-- Direct fulfillment (Mumbai)
(1, 'Oxygen Concentrator 10L', 'Dr. Smith (City Hospital)', 5, 1, 'Central Hub Mumbai', 1, 'Central Hub Mumbai', FALSE, 0.0, NOW() - INTERVAL '5 days', TRUE, NULL, 0),
(2, 'N95 Masks (Box of 50)', 'Priya Sharma (Metro Clinic)', 50, 1, 'Central Hub Mumbai', 1, 'Central Hub Mumbai', FALSE, 0.0, NOW() - INTERVAL '4 days', TRUE, NULL, 0),

-- Rerouted fulfillment (Requested at Delhi, fulfilled by Mumbai due to proximity/stock)
(5, 'Oxygen Concentrator 10L', 'Metro General Hospital', 10, 2, 'North Depot Delhi', 1, 'Central Hub Mumbai', TRUE, 1150.4, NOW() - INTERVAL '2 days', TRUE, NULL, 0),

-- Cancelled allocation test case
(7, 'Emergency First Aid Kit', 'Apollo Care Desk', 15, 2, 'North Depot Delhi', 2, 'North Depot Delhi', FALSE, 0.0, NOW() - INTERVAL '1 day', FALSE, 'admin@triage.com', 0),

-- Recent direct fulfillment (Bengaluru)
(8, 'Oxygen Concentrator 10L', 'St. John Health Center', 20, 3, 'South Logistics Bengaluru', 3, 'South Logistics Bengaluru', FALSE, 0.0, NOW() - INTERVAL '3 hours', TRUE, NULL, 0);

-- Sync PostgreSQL primary key sequence for allocation_records
SELECT setval('allocation_records_id_seq', (SELECT MAX(id) FROM allocation_records));

-- ------------------------------------------------------------------------------
-- 5. INVENTORY RESTOCK HISTORY
-- ------------------------------------------------------------------------------
INSERT INTO inventory_restock
(item_id, item_name, warehouse_id, warehouse_name, last_quantity_added, added_by, added_at)
VALUES
    (1, 'Oxygen Concentrator 10L', 1, 'Central Hub Mumbai', 50, 'mgr.mumbai@triage.com', NOW() - INTERVAL '10 days'),
    (2, 'N95 Masks (Box of 50)', 1, 'Central Hub Mumbai', 550, 'mgr.mumbai@triage.com', NOW() - INTERVAL '9 days'),
    (5, 'Oxygen Concentrator 10L', 2, 'North Depot Delhi', 20, 'admin@triage.com', NOW() - INTERVAL '8 days'),
    (8, 'Oxygen Concentrator 10L', 3, 'South Logistics Bengaluru', 100, 'mgr.blr@triage.com', NOW() - INTERVAL '7 days');

-- Sync PostgreSQL primary key sequence for inventory_restock
SELECT setval('inventory_restock_id_seq', (SELECT MAX(id) FROM inventory_restock));