-- creating dummy warehouses
INSERT INTO warehouses (name, latitude, longitude, is_active, version)
VALUES
    ('North Metro Depot', 40.7128, -74.0060, true, 0),
    ('Eastside Emergency Hub', 40.7306, -73.9352, true, 0);

--creating dummy inventory items
INSERT INTO inventory_items(warehouse_id, item_name, quantity_available, version)
VALUES
    (1, 'Trauma Medical Kit', 10, 0),
    (1, 'Portable Generator', 5, 0),
    (2, 'Trauma Medical Kit', 8, 0);

