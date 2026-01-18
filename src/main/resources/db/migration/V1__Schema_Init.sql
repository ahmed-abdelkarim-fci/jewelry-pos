-- ==========================================
-- 1. Configuration & Settings
-- ==========================================

CREATE TABLE system_setting (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(255)
);

-- Insert default settings
INSERT INTO system_setting (setting_key, setting_value) VALUES ('GOLD_AUTO_UPDATE', 'false');
INSERT INTO system_setting (setting_key, setting_value) VALUES ('HARDWARE_ENABLED', 'false');

-- ==========================================
-- 2. Security & RBAC Tables
-- ==========================================

CREATE TABLE app_user (
    id VARCHAR(26) PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    -- Audit Columns
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE roles (
    id VARCHAR(26) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    -- Audit Columns
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE permission (
    id VARCHAR(26) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    -- Audit Columns
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP
);

-- Join Tables for Users <-> Roles <-> Permissions
CREATE TABLE user_roles (
    user_id VARCHAR(26) NOT NULL,
    role_id VARCHAR(26) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE role_permissions (
    role_id VARCHAR(26) NOT NULL,
    permission_id VARCHAR(26) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
);

-- ==========================================
-- 3. Core Domain Tables (Inventory & Sales)
-- ==========================================

CREATE TABLE gold_rate (
    id VARCHAR(26) PRIMARY KEY,
    rate_24k NUMERIC(10, 2) NOT NULL,
    rate_21k NUMERIC(10, 2) NOT NULL,
    rate_18k NUMERIC(10, 2) NOT NULL,
    effective_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    -- Audit Columns
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE product (
    id VARCHAR(26) PRIMARY KEY,
    barcode VARCHAR(255) NOT NULL UNIQUE,
    model_name VARCHAR(255),
    purity VARCHAR(10),
    gross_weight NUMERIC(10, 3),
    making_charge NUMERIC(10, 2),
    version INT,
    status VARCHAR(20) NOT NULL, -- AVAILABLE, SOLD, RESERVED
    cost_price NUMERIC(12, 2) NOT NULL DEFAULT 0,
    -- Audit Columns
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE sale (
    id VARCHAR(26) PRIMARY KEY,
    transaction_date TIMESTAMP NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL, -- Gross Total (New Items)

    -- Payment Details (Updated for Old Gold)
    old_gold_total_value NUMERIC(12, 2) DEFAULT 0, -- Value deducted
    net_cash_paid NUMERIC(12, 2) DEFAULT 0,        -- Final cash taken

    customer_name VARCHAR(255),
    customer_phone VARCHAR(50),
    -- Audit Columns
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE sale_item (
    id VARCHAR(26) PRIMARY KEY,
    sale_id VARCHAR(26),
    product_id VARCHAR(26),
    applied_gold_rate NUMERIC(10, 2) NOT NULL,
    weight_snapshot NUMERIC(10, 3) NOT NULL,
    price_snapshot NUMERIC(10, 2) NOT NULL,
    -- Audit Columns
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP,
    FOREIGN KEY (sale_id) REFERENCES sale(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- ==========================================
-- 4. Old Gold & Scrap Inventory Tables
-- ==========================================

-- A. The "Scrap Box" in the Safe
CREATE TABLE scrap_inventory (
    karat VARCHAR(10) PRIMARY KEY, -- 'KARAT_21', 'KARAT_18'
    total_weight NUMERIC(12, 3) NOT NULL DEFAULT 0
);

-- Initialize defaults so the row always exists
INSERT INTO scrap_inventory (karat, total_weight) VALUES ('KARAT_24', 0);
INSERT INTO scrap_inventory (karat, total_weight) VALUES ('KARAT_21', 0);
INSERT INTO scrap_inventory (karat, total_weight) VALUES ('KARAT_18', 0);

-- B. Customer Transaction (Buying Old Gold)
CREATE TABLE old_gold_purchase (
    id VARCHAR(26) PRIMARY KEY,
    transaction_date TIMESTAMP NOT NULL,

    karat VARCHAR(10) NOT NULL,
    weight NUMERIC(10, 3) NOT NULL,
    buy_rate NUMERIC(10, 2) NOT NULL, -- The Buying Price per gram
    total_value NUMERIC(12, 2) NOT NULL,

    sale_id VARCHAR(26), -- Linked Sale ID (for Trade-ins/Estbdal)
    customer_national_id VARCHAR(50), -- Security requirement
    description VARCHAR(255),

    created_by VARCHAR(255) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sale(id)
);

-- C. Factory Transaction (Purification / Tasfya)
CREATE TABLE scrap_purification (
    id VARCHAR(26) PRIMARY KEY,
    transaction_date TIMESTAMP NOT NULL,

    karat VARCHAR(10) NOT NULL,
    weight_out NUMERIC(10, 3) NOT NULL, -- Weight removed from box
    cash_received NUMERIC(12, 2) NOT NULL,
    factory_name VARCHAR(255),

    created_by VARCHAR(255) NOT NULL
);

-- ==========================================
-- 5. Indexes for Performance
-- ==========================================

CREATE INDEX idx_product_barcode ON product(barcode);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_sale_date ON sale(transaction_date);
CREATE INDEX idx_sale_customer_name ON sale(customer_name);
CREATE INDEX idx_sale_customer_phone ON sale(customer_phone);

-- New Indexes for Old Gold
CREATE INDEX idx_old_gold_date ON old_gold_purchase(transaction_date);
CREATE INDEX idx_old_gold_national_id ON old_gold_purchase(customer_national_id);