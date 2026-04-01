-- ============================================================
--  FINAL CLEAN SCHEMA (MySQL + Docker Safe)
--  No duplicate index errors
--  No CTE syntax issues
--  Idempotent (can run multiple times safely)
-- ============================================================

DROP DATABASE IF EXISTS lbdrs_db;
CREATE DATABASE lbdrs_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE lbdrs_db;

-- ------------------------------------------------------------
-- Users
-- ------------------------------------------------------------
CREATE TABLE users (
    userId    INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      ENUM('Admin','MedicalStaff') NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Donor
-- ------------------------------------------------------------
CREATE TABLE donor (
    donorId          INT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(150) NOT NULL,
    age              INT NOT NULL CHECK (age BETWEEN 18 AND 65),
    gender           VARCHAR(10),
    bloodGroup       VARCHAR(5) NOT NULL,
    address          VARCHAR(255),
    location         VARCHAR(150),
    contactNumber    VARCHAR(20),
    isAvailable      BOOLEAN DEFAULT TRUE,
    lastDonationDate DATE,
    createdAt        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Donation History
-- ------------------------------------------------------------
CREATE TABLE donation_history (
    historyId      INT AUTO_INCREMENT PRIMARY KEY,
    donorId        INT NOT NULL,
    donationDate   DATE NOT NULL,
    donationStatus VARCHAR(50) DEFAULT 'Completed',
    location       VARCHAR(150),
    createdAt      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_donor FOREIGN KEY (donorId)
        REFERENCES donor(donorId) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Indexes (defined inline to avoid duplicates)
-- ------------------------------------------------------------
CREATE INDEX idx_blood_group   ON donor(bloodGroup);
CREATE INDEX idx_location      ON donor(location);
CREATE INDEX idx_available     ON donor(isAvailable);
CREATE INDEX idx_history_donor ON donation_history(donorId);

-- ------------------------------------------------------------
-- Users Seed
-- ------------------------------------------------------------
INSERT INTO users (username, password, role) VALUES
('admin', 'admin', 'Admin'),
('staff', 'staff', 'MedicalStaff');

-- ------------------------------------------------------------
-- Generate 5000 donors (NO CTE, Docker-safe)
-- ------------------------------------------------------------
INSERT INTO donor (
    name, age, gender, bloodGroup, address,
    location, contactNumber, isAvailable, lastDonationDate
)
SELECT
    CONCAT(
        ELT(FLOOR(1 + RAND(t.n) * 30),
            'Aarav','Aayush','Aditi','Aisha','Anisha','Arjun','Asmita','Bikash','Deepak','Dhruba',
            'Elina','Gaurav','Ishaan','Kabir','Kiran','Laxmi','Manish','Maya','Nabin','Niraj',
            'Nisha','Prabin','Prakash','Priya','Rajan','Ramesh','Rita','Rohan','Sagar','Sita'
        ),
        ' ',
        ELT(FLOOR(1 + RAND(t.n * 13) * 30),
            'Acharya','Adhikari','Aryal','Basnet','Bhandari','Bhattarai','Bista','Chaudhary','Dahal','Gautam',
            'Giri','Gurung','Karki','KC','Khadka','Lama','Magar','Maharjan','Neupane','Paudel',
            'Poudel','Rai','Regmi','Shah','Sharma','Shrestha','Subedi','Tamang','Thapa','Yadav'
        )
    ),
    FLOOR(18 + RAND() * 47),
    ELT(FLOOR(1 + RAND()*2), 'Male', 'Female'),
    ELT(FLOOR(1 + RAND()*8),
        'A+','A-','B+','B-','O+','O-','AB+','AB-'),
    CONCAT(t.n, ' Street'),
    ELT(FLOOR(1 + RAND()*5),
        'Kathmandu','Pokhara','Lalitpur','Bhaktapur','Biratnagar'),
    CONCAT('98', LPAD(t.n, 8, '0')),
    IF(RAND() > 0.2, TRUE, FALSE),
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND()*365) DAY)
FROM (
    SELECT a.N + b.N*10 + c.N*100 + d.N*1000 + 1 AS n
    FROM 
    (SELECT 0 N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a,
    (SELECT 0 N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b,
    (SELECT 0 N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c,
    (SELECT 0 N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) d
) t
LIMIT 5000;

-- ------------------------------------------------------------
-- Donation History Seed
-- ------------------------------------------------------------
INSERT INTO donation_history (
    donorId, donationDate, donationStatus, location
)
SELECT
    donorId,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND()*365) DAY),
    'Completed',
    CONCAT(location, ' Blood Center')
FROM donor
WHERE RAND() > 0.3;