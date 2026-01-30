-- =====================================================
-- Jewelry POS System - Initial Database Schema
-- Flyway Migration V1
-- =====================================================

-- =====================================================
-- 1. USER MANAGEMENT TABLES
-- =====================================================

-- Roles Table
CREATE TABLE roles (
    id VARCHAR(26) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

-- Permissions Table
CREATE TABLE permission (
    id VARCHAR(26) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

-- App User Table
CREATE TABLE app_user (
    id VARCHAR(26) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

-- User Roles Join Table
CREATE TABLE user_roles (
    user_id VARCHAR(26) NOT NULL,
    role_id VARCHAR(26) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Role Permissions Join Table
CREATE TABLE role_permissions (
    role_id VARCHAR(26) NOT NULL,
    permission_id VARCHAR(26) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
);

-- =====================================================
-- 2. PRODUCT & INVENTORY TABLES
-- =====================================================

-- Product Table
CREATE TABLE product (
    id VARCHAR(26) PRIMARY KEY,
    barcode VARCHAR(100) NOT NULL UNIQUE,
    model_name VARCHAR(255) NOT NULL,
    purity_enum VARCHAR(10) NOT NULL,
    type VARCHAR(50) NOT NULL,
    gross_weight DECIMAL(10,3),
    making_charge DECIMAL(10,2) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    cost_price DECIMAL(12,2) NOT NULL,
    version INTEGER DEFAULT 0,
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

-- Create index on barcode for fast lookups
CREATE INDEX idx_product_barcode ON product(barcode);
CREATE INDEX idx_product_status ON product(status);

-- =====================================================
-- 3. GOLD RATE TABLE
-- =====================================================

CREATE TABLE gold_rate (
    id VARCHAR(26) PRIMARY KEY,
    rate_24k DECIMAL(10,2) NOT NULL,
    rate_21k DECIMAL(10,2) NOT NULL,
    rate_18k DECIMAL(10,2) NOT NULL,
    effective_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

CREATE INDEX idx_gold_rate_effective_date ON gold_rate(effective_date DESC);
CREATE INDEX idx_gold_rate_active ON gold_rate(is_active);

-- =====================================================
-- 4. SALES TABLES
-- =====================================================

-- Sale Table
CREATE TABLE sale (
    id VARCHAR(26) PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50),
    transaction_date TIMESTAMP NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    old_gold_total_value DECIMAL(12,2) DEFAULT 0,
    net_cash_paid DECIMAL(12,2) DEFAULT 0,
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

CREATE INDEX idx_sale_transaction_date ON sale(transaction_date DESC);
CREATE INDEX idx_sale_customer_name ON sale(customer_name);
CREATE INDEX idx_sale_created_by ON sale(created_by);

-- Sale Item Table
CREATE TABLE sale_item (
    id VARCHAR(26) PRIMARY KEY,
    sale_id VARCHAR(26) NOT NULL,
    product_id VARCHAR(26) NOT NULL,
    applied_gold_rate DECIMAL(10,2) NOT NULL,
    weight_snapshot DECIMAL(10,3) NOT NULL,
    price_snapshot DECIMAL(10,2) NOT NULL,
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP,
    FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE INDEX idx_sale_item_sale_id ON sale_item(sale_id);
CREATE INDEX idx_sale_item_product_id ON sale_item(product_id);

-- =====================================================
-- 5. OLD GOLD & SCRAP MANAGEMENT TABLES
-- =====================================================

-- Old Gold Purchase Table
CREATE TABLE old_gold_purchase (
    id VARCHAR(26) PRIMARY KEY,
    transaction_date TIMESTAMP NOT NULL,
    purity VARCHAR(10) NOT NULL,
    weight DECIMAL(10,3) NOT NULL,
    buy_rate DECIMAL(10,2) NOT NULL,
    total_value DECIMAL(12,2) NOT NULL,
    sale_id VARCHAR(26),
    customer_national_id VARCHAR(50),
    customer_phone_number VARCHAR(50),
    description TEXT,
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

CREATE INDEX idx_old_gold_transaction_date ON old_gold_purchase(transaction_date DESC);
CREATE INDEX idx_old_gold_sale_id ON old_gold_purchase(sale_id);

-- Scrap Inventory Table
CREATE TABLE scrap_inventory (
    purity VARCHAR(10) PRIMARY KEY,
    total_weight DECIMAL(12,3) NOT NULL DEFAULT 0
);

-- Scrap Purification Table
CREATE TABLE scrap_purification (
    id VARCHAR(26) PRIMARY KEY,
    transaction_date TIMESTAMP NOT NULL,
    purity VARCHAR(10) NOT NULL,
    weight_out DECIMAL(10,3) NOT NULL,
    cash_received DECIMAL(12,2) NOT NULL,
    factory_name VARCHAR(255),
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

CREATE INDEX idx_scrap_purification_date ON scrap_purification(transaction_date DESC);

-- =====================================================
-- 6. SYSTEM SETTINGS TABLE
-- =====================================================

CREATE TABLE system_setting (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value VARCHAR(500)
);

-- =====================================================
-- 7. INITIAL DATA SEEDING
-- =====================================================

-- Insert default scrap inventory records
INSERT INTO scrap_inventory (purity, total_weight) VALUES ('KARAT_21', 0.000);
INSERT INTO scrap_inventory (purity, total_weight) VALUES ('KARAT_18', 0.000);

-- Insert default system settings
INSERT INTO system_setting (setting_key, setting_value) VALUES ('hardware_enabled', 'false');
INSERT INTO system_setting (setting_key, setting_value) VALUES ('gold_auto_update', 'false');

-- =====================================================
-- END OF MIGRATION V1
-- =====================================================
