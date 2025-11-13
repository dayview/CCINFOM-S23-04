-- RUN THIS IN MYSQL WORKBENCH BEFORE LOGIN IN
-- Common Login Credentials:
-- URL: jdbc:mysql://localhost:3306/business_database?useSSL=false&serverTimezone=UTC
-- Username: root
-- Password: 

CREATE DATABASE `business_database`;

USE `business_database`;

DROP TABLE IF EXISTS `inspector`;
DROP TABLE IF EXISTS `municipality`;
DROP TABLE IF EXISTS `business`;

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
);


CREATE TABLE `inspector` (
`inspector_id` INT NOT NULL AUTO_INCREMENT, 
`last_name` VARCHAR(35) NOT NULL DEFAULT '',
`first_name`VARCHAR(35) NOT NULL DEFAULT '',
`designation`VARCHAR(35) NOT NULL DEFAULT '',
`license_number`VARCHAR(35) NOT NULL DEFAULT '',
`active` TINYINT(1) NOT NULL DEFAULT 1,
`municipality_id` INT NOT NULL,
PRIMARY KEY (`inspector_id`),
FOREIGN KEY (`municipality_id`) REFERENCES `municipality`(`municipality_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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


INSERT INTO `inspector`
(`last_name`, `first_name`, `designation`, `license_number`, `active`, `municipality_id`)
VALUES
('Dela Cruz', 'Juan', 'Chief Building Inspector', 'LIC-2023-001', 1, 1),
('Reyes', 'Maria', 'Sanitation Inspector', 'LIC-2023-002', 1, 2),
('Santos', 'Jose', 'Electrical Inspector', 'LIC-2023-003', 1, 3),
('Garcia', 'Ana', 'Mechanical Inspector', 'LIC-2023-004', 1, 4),
('Villanueva', 'Carlos', 'Structural Inspector', 'LIC-2023-005', 1, 5),
('Cruz', 'Emilia', 'Plumbing Inspector', 'LIC-2023-006', 1, 6),
('Torres', 'Roberto', 'Fire Safety Inspector', 'LIC-2023-007', 0, 7),
('Domingo', 'Liza', 'Environmental Inspector', 'LIC-2023-008', 1, 8),
('Perez', 'Mark', 'Building Inspector', 'LIC-2023-009', 1, 9),
('Santiago', 'Angela', 'Occupational Safety Inspector', 'LIC-2023-010', 1, 10);

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