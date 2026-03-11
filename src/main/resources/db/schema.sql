-- Blood Donor Registry System Database Schema
-- This script creates the database and all necessary tables

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS blood_donor_registry;
USE blood_donor_registry;

-- Blood Types table
CREATE TABLE IF NOT EXISTS blood_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(5) NOT NULL UNIQUE COMMENT 'Blood type: A+, A-, B+, B-, AB+, AB-, O+, O-',
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type (type)
);

-- Locations table
CREATE TABLE IF NOT EXISTS locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_location (city, state, country),
    INDEX idx_city (city),
    INDEX idx_state (state)
);

-- Donors table
CREATE TABLE IF NOT EXISTS donors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15) UNIQUE,
    blood_type_id INT NOT NULL,
    location_id INT NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    last_donation_date DATE,
    availability BOOLEAN DEFAULT TRUE COMMENT 'Can donate or not',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (blood_type_id) REFERENCES blood_types(id),
    FOREIGN KEY (location_id) REFERENCES locations(id),
    INDEX idx_blood_type (blood_type_id),
    INDEX idx_location (location_id),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_name (name)
);

-- Donation History table
CREATE TABLE IF NOT EXISTS donation_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT NOT NULL,
    donation_date DATE NOT NULL,
    volume_collected INT COMMENT 'in milliliters',
    status ENUM('Completed', 'Pending', 'Failed', 'Cancelled') DEFAULT 'Completed',
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE,
    INDEX idx_donor (donor_id),
    INDEX idx_date (donation_date)
);

-- Medical Conditions table (for tracking ineligibility)
CREATE TABLE IF NOT EXISTS medical_conditions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT NOT NULL,
    condition_name VARCHAR(200) NOT NULL COMMENT 'e.g., Hypertension, Diabetes, etc.',
    diagnosis_date DATE,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE,
    INDEX idx_donor (donor_id)
);

-- Insert standard blood types
INSERT IGNORE INTO blood_types (type, description) VALUES
('O+', 'O Positive - Universal donor'),
('O-', 'O Negative - Universal donor for RBC'),
('A+', 'A Positive'),
('A-', 'A Negative'),
('B+', 'B Positive'),
('B-', 'B Negative'),
('AB+', 'AB Positive - Universal recipient'),
('AB-', 'AB Negative');

-- Insert sample locations
INSERT IGNORE INTO locations (city, state, country) VALUES
('New York', 'NY', 'USA'),
('Los Angeles', 'CA', 'USA'),
('Chicago', 'IL', 'USA'),
('Houston', 'TX', 'USA'),
('Phoenix', 'AZ', 'USA');

-- Create views for common queries
CREATE OR REPLACE VIEW donor_summary AS
SELECT 
    d.id,
    d.name,
    d.email,
    d.phone,
    bt.type AS blood_type,
    l.city,
    l.state,
    d.date_of_birth,
    d.gender,
    d.last_donation_date,
    d.availability,
    d.created_at
FROM donors d
JOIN blood_types bt ON d.blood_type_id = bt.id
JOIN locations l ON d.location_id = l.id;

CREATE OR REPLACE VIEW donation_statistics AS
SELECT 
    d.id,
    d.name,
    COUNT(dh.id) AS total_donations,
    SUM(dh.volume_collected) AS total_volume_donated,
    MAX(dh.donation_date) AS last_donation_date,
    bt.type AS blood_type
FROM donors d
LEFT JOIN donation_history dh ON d.id = dh.donor_id
JOIN blood_types bt ON d.blood_type_id = bt.id
GROUP BY d.id, d.name, bt.type;

COMMIT;
