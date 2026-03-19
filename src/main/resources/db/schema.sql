-- ============================================================
--  Local Blood Donor Registry System – Database Schema
--  Run once: mysql -u root -p < schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS lbdrs_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE lbdrs_db;

-- ------------------------------------------------------------
-- Users table  (authentication & role-based access control)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    userId    INT          AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,          -- BCrypt hash
    role      ENUM('Admin','MedicalStaff') NOT NULL,
    createdAt TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Donor table  (3NF)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS donor (
    donorId          INT          AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(150) NOT NULL,
    age              INT          NOT NULL CHECK (age BETWEEN 18 AND 65),
    gender           VARCHAR(10),
    bloodGroup       VARCHAR(5)   NOT NULL,
    address          VARCHAR(255),
    location         VARCHAR(150),
    contactNumber    VARCHAR(20),
    isAvailable      BOOLEAN      DEFAULT TRUE,
    lastDonationDate DATE,
    createdAt        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- DonationHistory table  (3NF – FK → donor)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS donation_history (
    historyId      INT          AUTO_INCREMENT PRIMARY KEY,
    donorId        INT          NOT NULL,
    donationDate   DATE         NOT NULL,
    donationStatus VARCHAR(50)  DEFAULT 'Completed',
    location       VARCHAR(150),
    createdAt      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_donor FOREIGN KEY (donorId)
        REFERENCES donor(donorId) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Indexes for fast search (blood group, location, availability)
-- ------------------------------------------------------------
CREATE INDEX idx_blood_group    ON donor(bloodGroup);
CREATE INDEX idx_location       ON donor(location);
CREATE INDEX idx_available      ON donor(isAvailable);
CREATE INDEX idx_history_donor  ON donation_history(donorId);

-- ------------------------------------------------------------
-- Default admin seed account
-- Password: Admin@123  (BCrypt hash below)
-- CHANGE THIS ON FIRST LOGIN in production
-- ------------------------------------------------------------
INSERT IGNORE INTO users (username, password, role)
VALUES ('admin',
        '$2a$12$eK9mCBtRcMbpWXd/T8kxS.IJ4U5Vb7UtCUOCZ0bTQN5z5p3oGU0/C',
        'Admin');

-- Default medical staff seed account
-- Password: Staff@123
INSERT IGNORE INTO users (username, password, role)
VALUES ('staff',
        '$2a$12$3e5RhvY9pQmZ7n1oAkJdXuWHbzCLVkI2MtX5yN8cFsD4rP0qLj6eW',
        'MedicalStaff');

-- Sample donor data
INSERT INTO donor (name, age, gender, bloodGroup, address, location, contactNumber, isAvailable, lastDonationDate) VALUES
('Alice Johnson',   28, 'Female', 'A+',  '12 Oak Street',    'London',     '07111111101', TRUE,  '2025-09-01'),
('Bob Smith',       34, 'Male',   'O-',  '45 Maple Avenue',  'Manchester', '07111111102', TRUE,  '2025-07-15'),
('Carol White',     22, 'Female', 'B+',  '8 Pine Road',      'Birmingham', '07111111103', FALSE, '2025-11-20'),
('David Brown',     45, 'Male',   'AB+', '99 Elm Lane',      'Leeds',      '07111111104', TRUE,  '2025-10-05'),
('Eva Garcia',      31, 'Female', 'O+',  '3 Cedar Close',    'Bristol',    '07111111105', TRUE,  '2025-08-30'),
('Frank Lee',       27, 'Male',   'A-',  '22 Birch Way',     'London',     '07111111106', TRUE,  '2025-06-12'),
('Grace Kim',       38, 'Female', 'B-',  '67 Willow Court',  'Sheffield',  '07111111107', FALSE, '2025-12-01'),
('Henry Patel',     52, 'Male',   'AB-', '14 Ash Drive',     'Liverpool',  '07111111108', TRUE,  '2025-05-20'),
('Isla Nguyen',     24, 'Female', 'A+',  '7 Poplar Street',  'London',     '07111111109', TRUE,  '2025-09-18'),
('James Wilson',    40, 'Male',   'O+',  '55 Sycamore Road', 'Manchester', '07111111110', TRUE,  '2025-04-10');

-- Sample donation history
INSERT INTO donation_history (donorId, donationDate, donationStatus, location) VALUES
(1, '2025-09-01', 'Completed', 'London Blood Bank'),
(1, '2025-03-10', 'Completed', 'London Blood Bank'),
(2, '2025-07-15', 'Completed', 'Manchester Royal'),
(3, '2025-11-20', 'Completed', 'Birmingham Centre'),
(4, '2025-10-05', 'Completed', 'Leeds General'),
(5, '2025-08-30', 'Completed', 'Bristol Infirmary'),
(6, '2025-06-12', 'Completed', 'London Blood Bank'),
(7, '2025-12-01', 'Deferred',  'Sheffield Hospital'),
(8, '2025-05-20', 'Completed', 'Liverpool Centre'),
(9, '2025-09-18', 'Completed', 'London Blood Bank'),
(10,'2025-04-10', 'Completed', 'Manchester Royal');
