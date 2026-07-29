-- warehouses
CREATE TABLE IF NOT EXISTS warehouses (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(200) NOT NULL,
                            latitude DOUBLE PRECISION NOT NULL,
                            longitude DOUBLE PRECISION NOT NULL,
                            is_active BOOLEAN NOT NULL DEFAULT TRUE,
                            version BIGINT NOT NULL DEFAULT 0
);

-- inventory items
CREATE TABLE IF NOT EXISTS inventory_items(
                                id BIGSERIAL PRIMARY KEY,
                                warehouse_id BIGINT NOT NULL,
                                item_name VARCHAR(200) NOT NULL,
                                quantity_available INT NOT NULL CONSTRAINT check_non_neg CHECK ( quantity_available >= 0 ),
                                version BIGINT NOT NULL DEFAULT 0,
    -- connecting warehouse to the inventory stock
                                CONSTRAINT foreign_key_war_invent FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE
);

-- index for fast lookup inside specific warehouse
CREATE INDEX idx_warehouse_item ON inventory_items(item_name, warehouse_id);

-- records for the allocated items
CREATE TABLE IF NOT EXISTS allocation_records (
                                    id BIGSERIAL PRIMARY KEY,
                                    item_id BIGINT NOT NULL,
                                    item_name VARCHAR NOT NULL,
                                    requester_name VARCHAR(200) NOT NULL,
                                    quantity_claimed INT NOT NULL,
                                    requested_war_id BIGINT NOT NULL,
                                    requested_war_name VARCHAR(200) NOT NULL,
                                    fulfilled_war_id BIGINT NOT NULL,
                                    fulfilled_war_name VARCHAR(200) NOT NULL,
                                    is_rerouted BOOLEAN NOT NULL,
                                    distance_km DOUBLE PRECISION NOT NULL,
                                    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                    is_active BOOLEAN NOT NULL,
                                    cancelled_by VARCHAR(200),
                                    version BIGINT NOT NULL DEFAULT 0,
                                    CONSTRAINT fk_item_transaction FOREIGN KEY (item_id) REFERENCES inventory_items(id)
);

-- inventory restock table
CREATE TABLE IF NOT EXISTS inventory_restock (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT,
    item_name VARCHAR(200) NOT NULL,
    warehouse_id BIGINT NOT NULL,
    warehouse_name VARCHAR(200) NOT NULL,
    last_quantity_added INT NOT NULL DEFAULT 0,
    added_by VARCHAR(200) NOT NULL,
    added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_history FOREIGN KEY(item_id) REFERENCES inventory_items(id) ON DELETE SET NULL
);

--user login and register table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL,
    password VARCHAR(200) NOT NULL,
    provider VARCHAR(200) NOT NULL,
    role VARCHAR(200) NOT NULL
);
