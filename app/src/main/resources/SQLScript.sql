-- RUN THIS IN MYSQL WORKBENCH BEFORE LOGGING IN
-- Common Login Credentials:
-- URL: jdbc:mysql://localhost:3306/business_database?useSSL=false&serverTimezone=UTC
-- Username: root
-- Password: <depending on user settings>

CREATE DATABASE `business_database`;

USE `business_database`;

DROP TABLE IF EXISTS `business`;
DROP TABLE IF EXISTS `owner`;
DROP TABLE IF EXISTS `inspector`;
DROP TABLE IF EXISTS `permit_type`;
DROP TABLE IF EXISTS `municipality`;

CREATE TABLE `business` (
  `business_id` INT AUTO_INCREMENT PRIMARY KEY,
  `business_name` VARCHAR(150) NOT NULL,
  `trade_name` VARCHAR(150),
  `barangay` VARCHAR(100),
  `street_address` VARCHAR(150),
  `business_type` VARCHAR(100),
  `tax_id` VARCHAR(50) UNIQUE,
  `start_date` DATE,
  `status` VARCHAR(50) NOT NULL DEFAULT '',
  `municipality_id` INT,
  FOREIGN KEY (municipality_id) REFERENCES municipality(municipality_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `owner` (
  `owner_id` INT NOT NULL AUTO_INCREMENT,
  `last_name` VARCHAR(35) NOT NULL DEFAULT '',
  `first_name` VARCHAR(35) NOT NULL DEFAULT '',
  `middle_name` VARCHAR(35) DEFAULT '',
  `contact_no` VARCHAR(15) NOT NULL, 
  `email` VARCHAR(35) NOT NULL,
  `gov_id_type` VARCHAR(35) NOT NULL,
  `gov_id_no` VARCHAR(35) NOT NULL,
  `tin` VARCHAR(35) NOT NULL,
  `home_address` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `inspector` (
  `inspector_id` INT NOT NULL AUTO_INCREMENT, 
  `last_name` VARCHAR(35) NOT NULL DEFAULT '',
  `first_name`VARCHAR(35) NOT NULL DEFAULT '',
  `middle_name`VARCHAR(35) NOT NULL DEFAULT '',
  `designation`VARCHAR(35) NOT NULL DEFAULT '',
  `license_number`VARCHAR(35) NOT NULL DEFAULT '',
  `active` TINYINT(1) NOT NULL DEFAULT 1,
  `municipality_id` INT NOT NULL,
  PRIMARY KEY (`inspector_id`),
  FOREIGN KEY (`municipality_id`) REFERENCES `municipality`(`municipality_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `permit_type` (
  `permit_type_id` INT NOT NULL AUTO_INCREMENT,
  `permit_name` VARCHAR(100) NOT NULL,
  `base_fee` DECIMAL(10,2) NOT NULL,
  `surcharge_rule` VARCHAR(200),
  `validity_months` INT NOT NULL,
  `document_requirements` TEXT,
  PRIMARY KEY (`permit_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `municipality` (
  `municipality_id` INT NOT NULL AUTO_INCREMENT,
  `municipality_name` VARCHAR(35) NOT NULL,
  `province` VARCHAR(35) NOT NULL,
  `region` VARCHAR(35) NOT NULL,
  `classification` VARCHAR(35),
  `contact_number` VARCHAR(15),
  `office_street` VARCHAR(150),
  `office_barangay` VARCHAR(100),
  `office_zipcode` VARCHAR(10),
  PRIMARY KEY (`municipality_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `business` 
(`business_name`, `trade_name`, `business_type`, `tax_id`, `start_date`, `status`, `street_address`, `barangay`, `municipality_id`)
VALUES
('SR Transport Logistics Corp.', 'SR Logistics', 'Transportation', 'TIN-400-001', '2020-01-15', 'Active', 'Lot 10, Industrial Park Ave.', 'Barangay Don Jose', 1),
('Pampanga Culinary Arts Center', 'Kulinarya PH', 'Restaurant/Education', 'TIN-400-002', '2019-05-20', 'Active', 'Unit 3 Commercial Complex', 'Sto. Niño', 2),
('Tagum Agri-Supply Hub', 'AgriHub', 'Retail/Agriculture', 'TIN-400-003', '2022-11-01', 'Pending', 'Apokon Road, near Public Market', 'Apokon', 3),
('Bayawan Ecotourism Resort', 'The Mangrove Retreat', 'Hospitality', 'TIN-400-004', '2018-08-10', 'Active', 'Coastal Highway, Sitio Kalikasan', 'Banga', 4),
('Isabela Grains and Milling', 'IGM Corp.', 'Manufacturing', 'TIN-400-005', '2023-03-25', 'Inactive', 'National Road, Zone 5', 'Alibagu', 5),
('Naga Digital Marketing Agency', 'Bicol Boost', 'Service/IT', 'TIN-400-006', '2021-09-12', 'Active', '4th Floor, CBD Plaza', 'Barangay Triangulo', 6),
('Vizcaya Hardware and Supplies', 'Vizcaya Build', 'Retail/Construction', 'TIN-400-007', '2020-07-07', 'Active', 'Main Street, Poblacion South', 'Poblacion', 7),
('Rizal Garment Export Inc.', 'RGEI', 'Manufacturing/Export', 'TIN-400-008', '2019-02-28', 'Active', 'Taytay Industrial Zone, Lot 22', 'San Juan', 8),
('Bohol Aquatic Farms', 'Aquafish Maribojoc', 'Agriculture/Fishery', 'TIN-400-009', '2024-01-05', 'Pending', 'Purok 1, Coastal Area', 'Poblacion', 9),
('Samar Power Systems', 'SPS Energy', 'Utilities/Service', 'TIN-400-010', '2017-06-19', 'Closed', 'Barangay Hall Road', 'Geracdo', 10);

INSERT INTO `owner`
(`last_name`, `first_name`, `middle_name`, `contact_no`, `email`, `gov_id_type`, `gov_id_no`, `tin`, `home_address`)
VALUES
('Aquino', 'Miguel', 'Santos', '0917-123-4567', 'maquino@email.com', 'Driver\'s License', 'N01-12-345678', '123-456-789-000', '123 Mabini St., Quezon City, Metro Manila'),
('Bautista', 'Sofia', 'Cruz', '0928-234-5678', 'sbautista@email.com', 'Passport', 'P1234567A', '234-567-890-000', '456 Rizal Ave., Makati City, Metro Manila'),
('Castillo', 'Ricardo', 'Lopez', '0939-345-6789', 'rcastillo@email.com', 'UMID', 'UMID-1234-5678-9012', '345-678-901-000', '789 Del Pilar St., San Fernando, Pampanga'),
('Mendoza', 'Elena', 'Reyes', '0945-456-7890', 'emendoza@email.com', 'SSS ID', 'SSS-01-2345678-9', '456-789-012-000', '321 Bonifacio St., Santa Rosa, Laguna'),
('Fernandez', 'Antonio', 'Garcia', '0956-567-8901', 'afernandez@email.com', 'PhilHealth ID', 'PH-12-345678901-2', '567-890-123-000', '654 Luna St., Naga City, Camarines Sur'),
('Gonzales', 'Patricia', 'Diaz', '0963-678-9012', 'pgonzales@email.com', 'Driver\'s License', 'N02-23-456789', '678-901-234-000', '987 Magsaysay Blvd., Tagum City, Davao del Norte'),
('Ramos', 'Ferdinand', 'Torres', '0974-789-0123', 'framos@email.com', 'Postal ID', 'POST-2023-123456', '789-012-345-000', '246 Aguinaldo Ave., Ilagan City, Isabela'),
('Santiago', 'Carmen', 'Villanueva', '0985-890-1234', 'csantiago@email.com', 'Voter\'s ID', 'VOTER-2023-789012', '890-123-456-000', '135 Osmena St., Bayawan City, Negros Oriental'),
('Morales', 'Daniel', 'Perez', '0996-901-2345', 'dmorales@email.com', 'TIN ID', '901-234-567-000', '901-234-567-000', '579 Quezon Blvd., Taytay, Rizal'),
('Valencia', 'Isabella', 'Santos', '0912-012-3456', 'ivalencia@email.com', 'PRC ID', 'PRC-2023-567890', '012-345-678-000', '864 Roxas Ave., Bayombong, Nueva Vizcaya');

INSERT INTO `inspector`
(`last_name`, `first_name`, `middle_name`, `designation`, `license_number`, `active`, `municipality_id`)
VALUES
('Dela Cruz', 'Juan', 'Perez', 'Chief Building Inspector', 'LIC-2023-001', 1, 1),
('Reyes', 'Maria', 'Santos', 'Sanitation Inspector', 'LIC-2023-002', 1, 2),
('Santos', 'Jose', 'Mariano', 'Electrical Inspector', 'LIC-2023-003', 1, 3),
('Garcia', 'Ana', 'Lim', 'Mechanical Inspector', 'LIC-2023-004', 1, 4),
('Villanueva', 'Carlos', 'Ramos', 'Structural Inspector', 'LIC-2023-005', 1, 5),
('Cruz', 'Emilia', 'Flores', 'Plumbing Inspector', 'LIC-2023-006', 1, 6),
('Torres', 'Roberto', 'Alonzo', 'Fire Safety Inspector', 'LIC-2023-007', 0, 7),
('Domingo', 'Liza', 'Chua', 'Environmental Inspector', 'LIC-2023-008', 1, 8),
('Perez', 'Mark', 'Bernardo', 'Building Inspector', 'LIC-2023-009', 1, 9),
('Santiago', 'Angela', 'Diaz', 'Occupational Safety Inspector', 'LIC-2023-010', 1, 10);

INSERT INTO `permit_type`
(`permit_name`, `base_fee`, `surcharge_rule`, `validity_months`, `document_requirements`)
VALUES
('Mayor\'s Permit', 5000.00, 'Late Renewal: 25% Surcharge after 30 days', 12, 'Barangay Clearance, Fire Safety Certificate, Sanitary Permit, Occupancy Permit, DTI/SEC Registration'),
('Sanitary Permit', 1500.00, 'Late Renewal: 500.00 Flat Surcharge', 12, 'Health Certificate, Sanitary Inspection Report, Business Layout Plan'),
('Fire Safety Inspection Certificate', 2000.00, 'Late Renewal: 10% Surcharge per Month', 12, 'Fire Safety Evaluation Clearance, Building Floor Plan, Certificate of Electrical Inspection'),
('Building Permit', 8000.00, 'Late Renewal: 1000.00 per Month delay', 24, 'Building Plans, Structural Design, Lot Plan, Tax Declaration, Occupancy Permit'),
('Zoning Clearance', 1000.00, 'No surcharge', 12, 'Location Plan, Tax Declaration, Land title or Contract of Lease'),
('Environmental Compliance Certificate', 3500.00, 'Late Renewal: 15% Surcharge', 36, 'Environmental Impact Assessment, Business Permit, Tax ID'),
('Occupancy Permit', 2500.00, 'Late Renewal: 20% Surcharge after 60 days', 0, 'Certificate of Completion, Approved Building Plans, Electrical Safety Certificate'),
('Health Certificate', 500.00, 'Late Renewal: 200.00 Flat Fee', 12, 'Medical Certificate, Chest X-Ray, Fecalysis Result'),
('Signage Permit', 1200.00, 'Late Renewal: 10% Surcharge', 12, 'Design and Layout of Signage, Lease Contract or Lot Title, Business Permit'),
('Liquor License', 10000.00, 'Late Renewal: 30% surcharge after 15 days', 12, 'Mayor\'s Permit, Police Clearance, Barangay Certification, SEC/DTI Registration');

INSERT INTO `municipality` 
(`municipality_name`, `province`, `region`, `classification`, `contact_number`, `office_street`, `office_barangay`, `office_zipcode`)
VALUES
('Santa Rosa', 'Laguna', 'Region IV-A (CALABARZON)', 'First Class City', '049-530-0017', 'City Hall, J.P. Rizal Blvd.', NULL, '4026'),
('San Fernando', 'Pampanga', 'Region III (Central Luzon)', 'First Class City', '045-961-6640', 'City Hall', 'Sto. Niño', '2000'),
('Tagum', 'Davao del Norte', 'Region XI (Davao Region)', 'First Class City', '084-655-9595', 'City Hall, Apokon Road', NULL, '8100'),
('Bayawan', 'Negros Oriental', 'Region VII (Central Visayas)', 'Second Class City', '035-430-0246', 'City Hall', 'Banga', '6221'),
('Ilagan', 'Isabela', 'Region II (Cagayan Valley)', 'First Class City', '078-624-9518', 'City Hall', 'Alibagu', '3300'),
('Naga', 'Camarines Sur', 'Region V (Bicol Region)', 'First Class City', '054-473-3442', 'City Hall Compound, J. Miranda Ave.', NULL, '4400'),
('Bayombong', 'Nueva Vizcaya', 'Region II (Cagayan Valley)', 'First Class Municipality', '078-321-2108', 'Municipal Hall, National Road', NULL, '3700'),
('Taytay', 'Rizal', 'Region IV-A (CALABARZON)', 'First Class Municipality', '02-658-7600', 'Municipal Hall, Rizal Ave.', NULL, '1920'),
('Maribojoc', 'Bohol', 'Region VII (Central Visayas)', 'Fourth Class Municipality', '038-537-9911', 'Municipal Hall, Poblacion', NULL, '6338'),
('Laoang', 'Northern Samar', 'Region VIII (Eastern Visayas)', 'Second Class Municipality', '055-251-9302', 'Municipal Hall, Barangay Geracdo', 'Geracdo', '6410');